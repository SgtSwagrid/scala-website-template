# Builds the production server into a small image that runs `app.jar` beside
# its static assets. See docs/DEPLOYMENT.md for how it reaches a server.

# Every build definition in the repository, and nothing else: the root build
# and any it includes by reference, wherever they are. Rebuilt on any change,
# but the layers below are reused while its output stays the same.
FROM alpine:3 AS definitions
COPY . /src
RUN find /src -type f \
      ! -name '*.sbt' ! -name .sbtopts ! -name .jvmopts ! -path '*/project/*' \
      -delete


FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /build

# Install whichever sbt the build asks for. It is run with `--server`, in the
# foreground, as a server left in the background dies with its layer.
COPY project/build.properties project/
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/* \
 && version=$(sed -n 's/^sbt\.version *= *//p' project/build.properties | tr -d '\r') \
 && curl -fsSL "https://github.com/sbt/sbt/releases/download/v$version/sbt-$version.tgz" \
  | tar -xz -C /opt \
 && ln -s /opt/sbt/bin/sbt /usr/local/bin/sbt

# Fetch dependencies from the build definitions alone, so that this layer is
# reused until a dependency changes rather than on every change to a source.
COPY --from=definitions /src ./
RUN sbt --server update

# Build the fat JAR, and gather it with the assets it serves into `dist`.
COPY . .
RUN sbt --server assemble \
 && mkdir dist \
 && mv app.jar dist/ \
 && cp -r "$(find target -type d -path '*/resource_managed/main/assets' | head -1)" dist/assets


FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN useradd --system --home-dir /app app \
 && mkdir data \
 && chown app:app data
COPY --from=build /build/dist ./
USER app

# Bind to all interfaces so the server is reachable outside the container, and
# let the heap grow with whatever memory the container is given.
ENV HOST=0.0.0.0
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
EXPOSE 8080

# Anything the server keeps on disk belongs here; mount a volume over it, or
# every restart starts afresh.
VOLUME /app/data

ENTRYPOINT ["java", "-Dassets.dir=/app/assets", "-jar", "app.jar"]
