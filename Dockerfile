# Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests
RUN rm -rf /root/.m2 /app/src /app/pom.xml

# Production stage for the backend
FROM bellsoft/liberica-openjdk-debian:21.0.1-12 AS production
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
WORKDIR /app
COPY --from=build /app/target/*.jar ./lukeria-erp-api.jar
RUN chown -R appuser:appgroup /app
USER appuser
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "lukeria-erp-api.jar"]
