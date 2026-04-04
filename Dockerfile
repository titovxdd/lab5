FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/lab6-server.jar /app/server.jar
COPY target/lab6-client.jar /app/client.jar
RUN mkdir -p /app/data /app/logs
EXPOSE 6767
CMD ["java", "-jar", "/app/server.jar"]