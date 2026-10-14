# ─────────────────────────────────────────────────────────────────────────────
# Generic Dockerfile for Spring Boot 4 / Java 21 microservices.
# Uses the Maven image directly — does NOT require mvnw or .mvn/ to be present.
# Copy this file into each microservice directory as: Dockerfile
# ─────────────────────────────────────────────────────────────────────────────

# ── Stage 1: build ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy POM first so dependency download is cached separately from source changes
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build the fat JAR (tests skipped — they require live infra)
COPY src/ src/
RUN mvn package -DskipTests -q

# ── Stage 2: runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Non-root user for basic hardening
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
