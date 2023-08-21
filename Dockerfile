FROM openjdk:17-jdk-slim-buster

WORKDIR /app

# Copy only the Gradle files first to leverage Docker's layer caching
COPY build.gradle .
COPY settings.gradle .
COPY gradlew .

# Build the application using Gradle
RUN ./gradlew build

# Copy the generated JAR file after the build
COPY build/libs/AlexBot-1.0-SNAPSHOT.jar .

EXPOSE 8080

CMD ["java", "-jar", "AlexBot-1.0-SNAPSHOT.jar"]