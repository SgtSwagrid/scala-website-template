# Deployment

Every push to `main` that passes CI is deployed to one Linux server:

1. [deploy.yml](../.github/workflows/deploy.yml) builds the [Dockerfile](../Dockerfile) into an image,
   tagged with the commit, and pushes it to the GitHub Container Registry (`ghcr.io/<owner>/<repo>`).
2. It copies [deploy/](../deploy) to the server over SSH, and there `docker compose` pulls the new
   image and replaces the running container.
3. [Caddy](https://caddyserver.com/) sits in front of the application. It serves HTTPS for your
   domain, and gets and renews the certificate on its own. One Caddy serves the whole server, so
   several applications can share one machine; see [Several applications on one
   server](#several-applications-on-one-server).
4. The workflow waits for `/health` to answer. If it doesn't, the workflow fails and prints the
   server's logs.

Nothing happens until the `HOSTNAME` variable is set, so a repository without a server just
skips the workflow. You can also run it by hand: **Actions → Deploy → Run workflow**.

## Setting up a server

You need a Linux server running Ubuntu or Debian, with a public IP address. 1 vCPU and 2 GB of
memory is plenty. Hetzner, DigitalOcean, Vultr and Lightsail all offer one for a few dollars a
month. The image is built on GitHub, so the server never compiles anything.

1. Copy [deploy/setup.sh](../deploy/setup.sh) to the server and run it as root:

   ```bash
   scp deploy/setup.sh root@<server>:
   ssh root@<server> bash setup.sh
   ```

   It installs Docker and opens ports 80 and 443. It also creates a `deploy` user and issues an
   SSH key for GitHub Actions. At the end it prints everything to enter in the next step.

2. In the repository, open **Settings → Secrets and variables → Actions** and add:

   | Kind     | Name             | Value                                                                |
   |----------|------------------|----------------------------------------------------------------------|
   | Variable | `HOSTNAME`       | The server's IP address or host name.                                |
   | Variable | `DEPLOY_USER`    | `deploy` (the default).                                              |
   | Variable | `DOMAIN`         | Optional. The site's domain, e.g. `app.example.com`.                 |
   | Variable | `KNOWN_HOSTS`    | The host keys printed by `setup.sh`. They pin the server's identity. |
   | Secret   | `DEPLOY_SSH_KEY` | The private key printed by `setup.sh`.                               |
   | Secret   | `ENVIRONMENT`    | Optional. The application's environment, one `NAME=value` per line.  |

   The host keys are public, as the server shows them to anyone who connects, so they are a
   variable: what matters is that nobody can change them, and a variable is as safe from that.

   The private key is not kept on the server, so copy it before closing the session. Running
   `setup.sh` again issues a new one in place of the old.

3. If you set `DOMAIN`, add a DNS `A` record that points it at the server. Caddy can't get a
   certificate until that record resolves. Without `DOMAIN`, the site is served over plain HTTP at
   the server's IP address, which works for a first look but shouldn't be used for sign-ins.

4. Run the workflow: **Actions → Deploy → Run workflow**.

The site must have a host of its own, such as `app.example.com`. A path under another site, such as
`example.com/app`, won't work: every URL the application produces starts from `/`.

## Several applications on one server

Only one process can hold ports 80 and 443, so the applications do not each bring their own Caddy.
`setup.sh` puts a single one at `/srv/proxy`, and every deployment installs its own site into
`/srv/proxy/sites/<repo>.caddy` and asks Caddy to read it.

Nothing is shared but the front door itself. No application edits another's configuration, none
knows the others exist, and a site that will not parse is refused by `caddy reload`, so a broken
deployment leaves every other site running on the last configuration that worked.

To add a second application to a server that already has one, in this order. The old application
holds ports 80 and 443 until step 3, and the front door cannot start until it lets go, so it is
down for the couple of minutes between steps 3 and 5.

1. Set `DOMAIN` on both repositories, and point a DNS `A` record for each at the server. At most
   one application on a server may go without a domain: a site with none answers on `:80` for any
   address, and two of those are an `ambiguous site definition` that Caddy refuses to load.
2. Merge the configuration update in the older repository, but do not deploy it yet.
3. Stop the old application on the server, which frees the ports:

   ```bash
   cd ~/<old-repo> && docker compose down
   ```
4. Copy `setup.sh` over and run it as root. It creates the `web` network and starts the front door.
   It also replaces the deploy key, so put the key it prints into `DEPLOY_SSH_KEY` on **both**
   repositories before going on, along with the host keys it prints as `KNOWN_HOSTS`.
5. Deploy the old application: **Actions → Deploy → Run workflow**. It comes back without a Caddy
   of its own, installs its site into the front door, and is served again.
6. Deploy the new application the same way.

To check the front door afterwards:

```bash
docker exec proxy caddy validate --config /etc/caddy/Caddyfile
ls /srv/proxy/sites/
docker logs --tail 20 proxy
```

### Adding a repository to a server that already has one

`setup.sh` mints one key, named `github-actions`, and replaces it on every run. Where several
repositories deploy to one server it is tidier to give each its own, so that one can be revoked
without disturbing the rest and so that re-running `setup.sh` leaves them alone. On the server, as
root, with `<repo>` in lower case:

```bash
work=$(mktemp -d); trap 'rm -rf "$work"' EXIT
ssh-keygen -q -t ed25519 -N "" -C <repo> -f "$work/key"
home=$(getent passwd deploy | cut -d: -f6)
install -d -m 700 -o deploy -g deploy "$home/.ssh"
touch "$home/.ssh/authorized_keys"
grep -v ' <repo>$' "$home/.ssh/authorized_keys" > "$work/authorized_keys" || true
cat "$work/key.pub" >> "$work/authorized_keys"
install -m 600 -o deploy -g deploy "$work/authorized_keys" "$home/.ssh/authorized_keys"
cat "$work/key"
```

That prints the private key for `DEPLOY_SSH_KEY`. It is never written to the server, and the
comment on the key is what keeps `setup.sh` and the snippet above from treading on each other.

`KNOWN_HOSTS` is the same for every repository on the server. `<host>` must be exactly the
value in `HOSTNAME`, since that is the name the workflow connects to:

```bash
awk -v host='<host>' '{ print host, $1, $2 }' /etc/ssh/ssh_host_*_key.pub
```

### Memory

Each application's JVM sizes its heap against whatever memory it can see, so two of them on one
small server will both try to take most of it and the kernel will decide which dies. Set the
`MEM_LIMIT` repository variable on each to divide the server up:

| Kind     | Name        | Value                                                            |
|----------|-------------|------------------------------------------------------------------|
| Variable | `MEM_LIMIT` | A Docker memory limit, such as `768m`. Defaults to `0`, no limit. |

A server with 2 GB of memory comfortably runs two applications at `768m` apiece, leaving room for
Caddy and the system. Leave it unset on a server running one application.

Do not add the limit to `deploy/compose.yml` directly: that file is synchronised from
[Scala Website Config](https://github.com/SgtSwagrid/scala-website-config) and local changes to it
are overwritten.

## Application environment

`ENVIRONMENT` becomes the container's environment. Anything the server reads from its environment,
such as API keys, belongs there, for example:

```
SOME_API_KEY=...
```

Leave out `HOST` and `PORT`. The image sets them.

## Data

Anything the server writes beneath `/app/data`, such as an embedded database, lives in the `data`
volume and survives every redeploy. Nothing else does. Back it up from the server:

```bash
cd ~/<repo>
docker compose stop app
docker run --rm -v <repo>_data:/data -v "$PWD":/backup alpine tar czf /backup/data.tgz -C /data .
docker compose start app
```

To start afresh, run `docker compose down` and then `docker volume rm <repo>_data`.

Removing an application altogether also means deleting its site from the front door:

```bash
rm /srv/proxy/sites/<repo>.caddy
docker exec proxy caddy reload --config /etc/caddy/Caddyfile
```

## Running the image locally

```bash
docker build -t app .
docker run --rm -p 8080:8080 -v app-data:/app/data app
```

Then open [localhost:8080](http://localhost:8080).

## Rolling back

Every deployed commit keeps its image. To run an older one, re-run that commit's **Deploy**
workflow from the Actions tab. Alternatively, on the server, set `IMAGE` in `~/<repo>/.env` to
`ghcr.io/<owner>/<repo>:<commit>` and run `docker compose up -d`. Pulling from the registry
requires signing in to it first, as the workflow does.
