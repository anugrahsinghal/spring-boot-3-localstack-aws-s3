# syntax=docker/dockerfile:1
FROM eclipse-temurin:17


COPY .mvn .mvn/
COPY mvnw mvnw
COPY pom.xml pom.xml

RUN ./mvnw verify --fail-never

COPY src src/
COPY package.json package.json
COPY webpack.config.js webpack.config.js

# RUN ./mvnw install

EXPOSE 8080

ENTRYPOINT ["./mvnw","spring-boot:run"]