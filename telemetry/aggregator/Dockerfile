FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY target/aggregator-1.0-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]