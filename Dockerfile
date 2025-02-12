FROM openjdk:17-jdk-slim-buster
WORKDIR /app

COPY build/libs/service.jar .

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "service.jar"]
