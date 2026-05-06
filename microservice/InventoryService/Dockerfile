FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY microservice/InventoryService/target/InventoryService-0.1.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8085