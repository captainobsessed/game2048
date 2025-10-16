# ===================================================================================
# Stage 1: The Build Stage
#
# This stage uses a full JDK and Maven image to build our application's JAR file.
# The resulting image from this stage will be discarded; we only care about the
# compiled artifact.
# ===================================================================================
FROM maven:3.8-openjdk-8 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml first to leverage Docker's layer caching.
# If the pom.xml hasn't changed, Docker will reuse the dependency layer,
# speeding up builds significantly.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Package the application, skipping the tests.
# Tests should be run in a separate CI/CD pipeline step.
RUN mvn package -DskipTests

# ===================================================================================
# Stage 2: The Final Runtime Stage
#
# This stage uses a lightweight JRE image. It will copy ONLY the JAR file
# from the 'builder' stage, resulting in a small, secure, and efficient
# final image.
# ===================================================================================
FROM openjdk:8-jre-slim

# Set the working directory
WORKDIR /app

# Create a non-root user and group for security purposes.
# Running applications as a non-root user is a critical security best practice.
RUN groupadd -r spring && useradd -r -g spring appuser

# Switch to the non-root user
USER appuser

# Copy the built JAR file from the 'builder' stage.
# The wildcard is used to avoid hardcoding the version number.
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# The command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
