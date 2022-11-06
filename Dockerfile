# syntax=docker/dockerfile:1
FROM eclipse-temurin:17


COPY .mvn .mvn/
COPY mvnw mvnw
COPY pom.xml pom.xml

RUN ./mvnw verify --fail-never

COPY src src/

# RUN ./mvnw install

EXPOSE 8080

ENTRYPOINT ["./mvnw","spring-boot:run"]