# ---- Stage 1: Build ----
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# Copy everything and build the JAR
COPY . .
RUN gradle clean bootJar --no-daemon

# ---- Stage 2: Run ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port and run the app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
