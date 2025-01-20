FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

# Production stage for the frontend
FROM bellsoft/liberica-openjdk-debian:17.0.13-12 AS production
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
WORKDIR /app
COPY --from=build /app/target/*.jar ./lukeria-frontend.jar
RUN chown -R appuser:appgroup /app
USER appuser
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "lukeria-frontend.jar"]
