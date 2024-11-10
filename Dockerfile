#1: user maven for building

FROM maven:3.6.3-openjdk-17-slim AS build

WORKDIR /app
COPY . ./CourseSpringBE
WORKDIR /app/CourseSpringBE
RUN mvn clean package -DskipTests

#2: user java for running

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/CourseSpringBE/target/*.jar /app/CourseSpringBE.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "CourseSpringBE.jar"]

