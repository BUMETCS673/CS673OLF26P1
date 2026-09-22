# Stage 1: Build Java 21 app with Maven
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Download dependencies first (cached unless pom.xml changes)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build (NO 'clean' needed in Docker)
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Java 21 Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]