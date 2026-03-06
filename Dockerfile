FROM eclipse-temurin:17-jre
COPY ./target/lab5-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]