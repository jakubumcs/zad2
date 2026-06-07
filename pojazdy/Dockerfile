# ---- Stage 1: Build ----
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src/ src/
RUN mvn package -DskipTests -q

# ---- Stage 2: Run ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/pojazdy-1.0-SNAPSHOT.jar app.jar
COPY users.json .
COPY vehicles.json .
COPY rentals.json .
COPY categories.json .
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -qO- http://localhost:8080/api/vehicles || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]