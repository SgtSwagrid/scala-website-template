FROM mcr.microsoft.com/devcontainers/base:ubuntu-24.04

ARG DEBIAN_FRONTEND=noninteractive

# Install runtime dependencies for Coursier and JVM-based tooling.
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        ca-certificates \
        curl \
        gzip \
        bash \
        openjdk-21-jre-headless \
    && rm -rf /var/lib/apt/lists/*

# Install scala, scalac, and sbt via Coursier into /usr/local/bin.
RUN curl -fsSL https://github.com/coursier/launchers/raw/master/cs-x86_64-pc-linux.gz \
      | gzip -d > /usr/local/bin/cs \
    && chmod +x /usr/local/bin/cs \
    && /usr/local/bin/cs install --install-dir /usr/local/bin scala scalac sbt
