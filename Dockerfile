# ============================================================
# Stage 1: Build
# ============================================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy only build config first for dependency caching
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw mvnw.cmd ./

RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source and build the application
COPY src src
RUN ./mvnw clean package -DskipTests -B

# ============================================================
# Stage 2: Runtime
# ============================================================
FROM eclipse-temurin:21-jre-alpine

# Install tini for proper signal handling (PID 1)
RUN apk add --no-cache tini

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Image labels for better container metadata
LABEL org.opencontainers.image.title="AkatsukiReportBase" \
      org.opencontainers.image.description="Akatsuki News Report Base Application" \
      org.opencontainers.image.version="0.0.1-SNAPSHOT" \
      org.opencontainers.image.vendor="Akatsuki News Application"

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Production JVM flags
# - UseContainerSupport: Enable container-aware memory/CPU limits
# - MaxRAMPercentage: Use up to 75% of available container memory
# - AlwaysPreTouch: Pre-touch all heap pages for consistent performance
# - java.security.egd: Use non-blocking entropy source for faster startup
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+AlwaysPreTouch \
               -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

USER appuser

# Health check to verify the application is running
HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 \
    CMD wget -qO- http://localhost:8080/ || exit 1

# Use tini as init process for proper signal forwarding (SIGTERM → graceful shutdown)
ENTRYPOINT ["/sbin/tini", "--"]
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
