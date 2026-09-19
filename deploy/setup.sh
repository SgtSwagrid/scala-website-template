#!/usr/bin/env bash
# Prepares a fresh Ubuntu or Debian server to receive deployments: installs
# Docker, opens the web ports, and creates a `deploy` user which GitHub Actions
# signs in as. Copy it over and run it once, as root:
#
#   scp deploy/setup.sh root@<server>:
#   ssh root@<server> bash setup.sh
#
# Running it again changes nothing that is already set up.

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

# A key pair for GitHub Actions alone, so it can be revoked without touching
# anyone else's access.
home=$(getent passwd "$user" | cut -d: -f6)
install -d -m 700 -o "$user" -g "$user" "$home/.ssh"
key="$home/.ssh/github-actions"
if [ ! -f "$key" ]; then
  ssh-keygen -q -t ed25519 -N "" -C github-actions -f "$key"
  cat "$key.pub" >> "$home/.ssh/authorized_keys"
  chown "$user:$user" "$key" "$key.pub" "$home/.ssh/authorized_keys"
  chmod 600 "$home/.ssh/authorized_keys"
fi

host=$(curl -fsS https://api.ipify.org || hostname -I | cut -d' ' -f1)

cat <<INSTRUCTIONS

Done. Now, in the repository on GitHub, under
Settings -> Secrets and variables -> Actions, add:

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

Then delete the private key from this server:
  rm $key

INSTRUCTIONS
