#1: user maven for building

FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline

COPY . .
RUN mvn clean package -DskipTests

#2: user java for running
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=BUILD /app/target/*.jar app.jar
COPY --from=BUILD /app/src/main/resources /app/src/main/resources

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

