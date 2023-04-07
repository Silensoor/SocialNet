FROM maven:3.8.6-jdk-11-slim
WORKDIR /app
COPY target/backend.jar /app
COPY src/main/resources/application-deploy.yml /app/application.yml
RUN apt-get update && apt-get install -y fontconfig
ENTRYPOINT ["java", "-jar", "/app/backend.jar"]
