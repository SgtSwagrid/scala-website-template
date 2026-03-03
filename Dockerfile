FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /build

COPY . .
RUN sbt assemble


FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /build/app.jar app.jar

# Bind to all interfaces so the server is reachable outside the container.
ENV HOST=0.0.0.0
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
