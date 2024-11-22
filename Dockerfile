FROM gradle:8.10.2-jdk23-alpine AS build

WORKDIR /app

COPY backend/gradlew ./
COPY backend/gradle ./gradle

RUN chmod +x ./gradlew

RUN ./gradlew build --no-daemon

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/springbootDemo-0.0.1-SNAPSHOT.jar ./dockertest.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "dockertest.jar"]
