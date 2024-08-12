FROM openjdk:21

WORKDIR /app

COPY target/app.jar /app/app.jar

EXPOSE 80
EXPOSE 5433
EXPOSE 6300

CMD ["java", "-jar", "app.jar"]