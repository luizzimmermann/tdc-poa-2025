###########
# Builder #
###########
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /app

# Copy Gradle wrapper and build files first (better layer caching)
COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

RUN chmod +x gradlew

# Copy source code
COPY src ./src

# Build the Spring Boot fat JAR
RUN ./gradlew bootJar --no-daemon

############
# Runtime  #
############
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
