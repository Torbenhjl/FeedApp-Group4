FROM gradle:8.11.1-jdk21 AS build

WORKDIR /app

COPY ./backend/ ./

RUN ls -l

RUN chmod +x ./gradlew

RUN ls -l

RUN ./gradlew build --no-daemon

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/oblig1-0.0.1-SNAPSHOT.jar ./dockertest.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "dockertest.jar"]
