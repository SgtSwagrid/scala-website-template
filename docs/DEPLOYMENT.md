# Deployment

Every push to `main` that passes CI is deployed to one Linux server:

1. [deploy.yml](../.github/workflows/deploy.yml) builds the [Dockerfile](../Dockerfile) into an image,
   tagged with the commit, and pushes it to the GitHub Container Registry (`ghcr.io/<owner>/<repo>`).
2. It copies [deploy/](../deploy) to the server over SSH, and there `docker compose` pulls the new
   image and replaces the running container.
3. [Caddy](https://caddyserver.com/) sits in front of the application. It serves HTTPS for your
   domain, and gets and renews the certificate on its own.
4. The workflow waits for `/health` to answer. If it doesn't, the workflow fails and prints the
   server's logs.

Nothing happens until the `DEPLOY_HOST` variable is set, so a repository without a server just
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

   It installs Docker and opens ports 80 and 443. It also creates a `deploy` user and gives that
   user a new SSH key for GitHub Actions. At the end it prints everything to enter in the next step.

2. In the repository, open **Settings → Secrets and variables → Actions** and add:

   | Kind     | Name                 | Value                                                              |
   |----------|----------------------|--------------------------------------------------------------------|
   | Variable | `DEPLOY_HOST`        | The server's IP address or host name.                              |
   | Variable | `DEPLOY_USER`        | `deploy` (the default).                                            |
   | Variable | `DOMAIN`             | Optional. The site's domain, e.g. `app.example.com`.               |
   | Secret   | `DEPLOY_SSH_KEY`     | The private key printed by `setup.sh`.                             |
   | Secret   | `DEPLOY_KNOWN_HOSTS` | The host keys printed by `setup.sh`. They pin the server's identity. |
   | Secret   | `APP_ENV`            | Optional. The application's environment, one `NAME=value` per line. |

   Then delete the private key from the server, using the command `setup.sh` printed.

3. If you set `DOMAIN`, add a DNS `A` record that points it at the server. Caddy can't get a
   certificate until that record resolves. Without `DOMAIN`, the site is served over plain HTTP at
   the server's IP address, which works for a first look but shouldn't be used for sign-ins.

4. Run the workflow: **Actions → Deploy → Run workflow**.

The site must have a host of its own, such as `app.example.com`. A path under another site, such as
`example.com/app`, won't work: every URL the application produces starts from `/`.

## Application environment

`APP_ENV` becomes the container's environment. Anything the server reads from its environment,
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
