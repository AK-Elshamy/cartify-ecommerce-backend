# Stage 1: Build the application using Maven Wrapper
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml to cache dependencies
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code and build the JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Lightweight runtime image
FROM eclipse-temurin:25-jre
WORKDIR /app

# Create non-root user and uploads directory
RUN groupadd -r spring && useradd -r -g spring spring \
    && mkdir -p /app/uploads \
    && chown -R spring:spring /app

USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
