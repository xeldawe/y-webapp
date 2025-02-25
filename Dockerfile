# Stage 1: Build the application
FROM gradle:8.12.1-jdk21 AS build

# Set the working directory
WORKDIR /home/gradle/src

# Copy the Gradle wrapper and build files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy the application source code
COPY src ./src

# Ensure the gradlew file has executable permissions for Unix systems
RUN chmod +x gradle

# Build the application
RUN gradle build

# Stage 2: Create the runtime image
FROM openjdk:23

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar

# Expose the port that the application will run on
EXPOSE 8080

# Set the entry point to run the jar file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
