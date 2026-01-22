FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy pre-built JAR
COPY build/libs/*.jar app.jar

# Expose port and run the app
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -Dspring.config.location=${SPRING_CONFIG_LOCATION:-classpath:/} -Dapplication.brand.botConfigPath=${APPLICATION_BRAND_BOTCONFIGPATH:-} -jar app.jar"]
