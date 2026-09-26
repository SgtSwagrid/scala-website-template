#!/usr/bin/env bash

# Prepares a fresh Ubuntu or Debian server to receive deployments:
#   - Installs Docker
#   - Opens the web ports (80, 443)
#   - Runs a common application gateway in front of any other deployments
#   - Creates a 'deploy' user for GitHub Actions to sign in as
#
# Copy the script to the server, and run it once (as root):
#
#   scp deploy/setup.sh root@<server>:
#   ssh root@<server> bash setup.sh

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

# Configure and run a reverse proxy, shared by all web applications.

proxy=/srv/proxy
install -d -m 755 "$proxy"
install -d -m 775 -o "$user" -g "$user" "$proxy/sites"

docker network inspect web >/dev/null 2>&1 || docker network create web

cat > "$proxy/Caddyfile" <<'CADDYFILE'
# The common application gateway, started once with setup.sh.
# Each application is isolated and installs its own file under 'sites/'.

import /etc/caddy/sites/*.caddy
CADDYFILE

cat > "$proxy/compose.yml" <<'COMPOSE'
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
  caddy-data:
  caddy-config:
COMPOSE

( cd "$proxy" && docker compose up -d )

# Generate a public-private key pair for deployments from GitHub.

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

rm -f "$home/.ssh/github-actions" "$home/.ssh/github-actions.pub"

host=$(curl -fsS https://api.ipify.org || hostname -I | cut -d' ' -f1)

cat <<INSTRUCTIONS

Done. The application gateway is running, and is ready to dispatch incoming requests.

Further action is required to enable automatic deployment:

In the repository on GitHub, under 'Settings → Secrets and variables → Actions', add:

Variables:

  HOSTNAME    = $host
  DEPLOY_USER = $user
  DOMAIN      = (optional) a domain whose DNS A record points at $host

  KNOWN_HOSTS =
$(awk -v host="$host" '{ print host, $1, $2 }' /etc/ssh/ssh_host_*_key.pub)

Secrets:

  DEPLOY_SSH_KEY =
$(cat "$key")

  ENVIRONMENT = The application's environment, as one 'KEY=VALUE'-pair per line (optional).

The private key is not stored on the server, so be sure to save it now.
Running this script again will generate a fresh key, rendering the old one inactive.

INSTRUCTIONS
