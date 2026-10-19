FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY . .
ARG SERVICE_MODULE
RUN mvn -B -pl ${SERVICE_MODULE} -am package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
ARG SERVICE_MODULE
COPY --from=build /workspace/${SERVICE_MODULE}/target/${SERVICE_MODULE}-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
