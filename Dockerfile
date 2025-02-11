FROM openjdk:22-jdk-slim

WORKDIR /app

COPY --chown=root:root target/fin-0.0.1-SNAPSHOT.jar /app/testcicd.jar

EXPOSE 8083

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/testcicd.jar"]