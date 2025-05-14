 # Use a Java 17 base image
FROM eclipse-temurin:17-jdk-alpine AS build

# Set working directory
WORKDIR /app

# Copy gradle files for dependency resolution
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Give execution permissions to gradlew
RUN chmod +x ./gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build the application
RUN ./gradlew build --no-daemon -x test

# Create a lightweight runtime image
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built jar file
COPY --from=build /app/build/libs/*.jar app.jar

# Environment variables
ENV PORT=8080 \
    MONGODB_HOST=mongodb \
    MONGODB_PORT=27017 \
    MONGODB_DATABASE=verification \
    REDIS_HOST=redis \
    REDIS_PORT=6379 \
    PRIMARY_PROVIDER_URL=https://api.telecom-provider.com \
    FALLBACK_PROVIDER_URL=https://api.fallback-provider.com \
    LOG_LEVEL=INFO

# Expose port
EXPOSE $PORT

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:$PORT/api/v1/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
