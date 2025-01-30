FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
WORKDIR /app
RUN mvn clean package -DskipTests

FROM openjdk:21-slim
WORKDIR /app
COPY --from=build app/target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]