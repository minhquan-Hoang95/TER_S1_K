# ---- Stage 1: Build ----
FROM gradle:8.10-jdk21 AS build
WORKDIR /app
COPY . .
# build Spring Boot jar
RUN gradle clean bootJar --no-daemon

# ---- Stage 2: Run ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy only the runnable jar
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

# Configure the port
ENV PORT=8080
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
