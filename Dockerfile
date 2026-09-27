# Packs the production server into a small image that runs `app.jar` beside
# its static assets. Nothing is built here: `deploy/package.sh` builds both
# into `dist`, which CI does once on main, from the build it has tested. See
# docs/DEPLOYMENT.md for how the image reaches a server.

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN useradd --system --home-dir /app app \
 && mkdir data \
 && chown app:app data
COPY dist ./
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
