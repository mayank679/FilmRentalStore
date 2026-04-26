# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests -f frontend/pom.xml

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/frontend/target/frontend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]
