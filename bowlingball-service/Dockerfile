# Use OpenJDK 17 as base
FROM openjdk:17-jdk-slim

# Set working directory inside container
WORKDIR /app

# Copy the built JAR
COPY build/libs/bowlingball-service-1.0.0-SNAPSHOT.jar app.jar

# Expose internal port (matches application.yml)
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
