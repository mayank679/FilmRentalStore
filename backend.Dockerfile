# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests -f backend/pom.xml

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/backend/target/Film_Rental_System-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]
