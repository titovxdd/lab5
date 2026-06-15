FROM eclipse-temurin:17-jre
WORKDIR /app
COPY dbconfig.properties /app/
COPY target/lab6-server.jar /app/server.jar
COPY target/lab6-client.jar /app/client.jar
EXPOSE 6767
CMD ["java", "-cp", "server.jar", "com.lab6.server.Server"]