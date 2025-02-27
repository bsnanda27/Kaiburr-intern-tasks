# Use a lightweight JDK base image
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/kaiburr-0.0.1-SNAPSHOT.jar task-api.jar

EXPOSE 8080

CMD ["java", "-jar", "task-api.jar"]
