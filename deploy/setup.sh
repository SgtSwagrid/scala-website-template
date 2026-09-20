#!/usr/bin/env bash
# Prepares a fresh Ubuntu or Debian server to receive deployments: installs
# Docker, opens the web ports, puts a shared front door in front of whatever
# gets deployed, and creates a `deploy` user which GitHub Actions signs in as.
# Copy it over and run it once, as root:
#
#   scp deploy/setup.sh root@<server>:
#   ssh root@<server> bash setup.sh
#
# Running it again leaves everything already set up as it is, except that it
# issues a fresh key pair, so the secrets it prints must be entered again.

set -euo pipefail

user=deploy

if ! command -v docker >/dev/null; then
  curl -fsSL https://get.docker.com | sh
fi

if ! id "$user" >/dev/null 2>&1; then
  useradd --create-home --shell /bin/bash "$user"
fi
usermod -aG docker "$user"

if command -v ufw >/dev/null && ufw status | grep -q active; then
  ufw allow 80/tcp
  ufw allow 443/tcp
  ufw allow 443/udp
fi

# -------------------------------------------------------------------------------------------------
# The shared front door.
#
# One Caddy owns ports 80 and 443 and serves every application on the server,
# because only one process can hold those ports. Each application installs a
# file of its own under `sites/` when it deploys and reloads Caddy; nothing
# here is edited by an application, and no application knows about any other.
#
# A configuration that fails to parse is refused by `caddy reload`, so a broken
# deployment leaves every site running on the last good configuration.
# -------------------------------------------------------------------------------------------------

proxy=/srv/proxy
install -d -m 755 "$proxy"
install -d -m 775 -o "$user" -g "$user" "$proxy/sites"

docker network inspect web >/dev/null 2>&1 || docker network create web

cat > "$proxy/Caddyfile" <<'CADDYFILE'
# The front door. Each application installs its own file under `sites/`; this
# one holds nothing but the instruction to read them.
#
# The admin API is deliberately left on. It listens on localhost inside the
# container, is published nowhere, and is what `caddy reload` speaks to when an
# application installs its site.

import /etc/caddy/sites/*.caddy
CADDYFILE

cat > "$proxy/compose.yml" <<'COMPOSE'
# The shared front door. Started once by setup.sh; applications never touch it.
services:

  caddy:
    image: caddy:2
    container_name: proxy
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
      - "443:443/udp"
    volumes:
      - ./Caddyfile:/etc/caddy/Caddyfile:ro
      - ./sites:/etc/caddy/sites:ro
      - caddy-data:/data
      - caddy-config:/config
    networks:
      - web

networks:
  web:
    external: true

volumes:
  # Caddy's certificates, kept so that redeploys don't request new ones.
  caddy-data:
  caddy-config:
COMPOSE

( cd "$proxy" && docker compose up -d )

# A key pair for GitHub Actions alone, so it can be revoked without touching
# anyone else's access. The private key is printed below and never written to
# the server, so a run replaces whatever key the previous run issued.
work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT
key="$work/github-actions"
ssh-keygen -q -t ed25519 -N "" -C github-actions -f "$key"

home=$(getent passwd "$user" | cut -d: -f6)
authorized="$home/.ssh/authorized_keys"
install -d -m 700 -o "$user" -g "$user" "$home/.ssh"
touch "$authorized"
grep -v ' github-actions$' "$authorized" > "$work/authorized_keys" || true
cat "$key.pub" >> "$work/authorized_keys"
install -m 600 -o "$user" -g "$user" "$work/authorized_keys" "$authorized"

# Earlier versions of this script left the private key on the server.
rm -f "$home/.ssh/github-actions" "$home/.ssh/github-actions.pub"

host=$(curl -fsS https://api.ipify.org || hostname -I | cut -d' ' -f1)

cat <<INSTRUCTIONS

Done. The front door is running, and every application deployed to this server
will be served through it. Running this script again leaves it alone.

Now, in the repository on GitHub, under
Settings → Secrets and variables → Actions, add:

Variables:
  DEPLOY_HOST = $host
  DEPLOY_USER = $user
  DOMAIN      = (optional) a domain whose DNS A record points at $host

Secrets:
  DEPLOY_SSH_KEY =
$(cat "$key")

  DEPLOY_KNOWN_HOSTS =
$(awk -v host="$host" '{ print host, $1, $2 }' /etc/ssh/ssh_host_*_key.pub)

  APP_ENV = (optional) the application's environment, one NAME=value per line

The private key is not kept on the server, so copy it now. Run this script
again if you need another one.

INSTRUCTIONS
