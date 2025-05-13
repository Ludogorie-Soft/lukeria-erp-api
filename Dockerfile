# Build stage
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build

WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests
RUN rm -rf /root/.m2 /app/src /app/pom.xml

# Production stage
FROM eclipse-temurin:17-jre-alpine

# Add non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=build /app/target/*.jar ./lukeria-erp-api.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "/app/lukeria-erp-api.jar"]

