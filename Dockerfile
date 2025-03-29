
# Stage 1: Build with JDK
FROM eclipse-temurin:17-jdk-alpine as builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package

# Stage 2: Run with JRE (smaller)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/splitpay-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 7777
ENTRYPOINT ["java", "-jar", "app.jar"]