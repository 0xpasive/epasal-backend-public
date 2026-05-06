FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY microservice/CartService/target/CartService-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8083