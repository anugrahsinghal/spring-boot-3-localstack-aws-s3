# syntax=docker/dockerfile:1
#FROM maven:3-jdk-17 as builder
FROM eclipse-temurin:17


COPY .mvn .mvn/
COPY mvnw mvnw
COPY pom.xml pom.xml

RUN ./mvnw verify --fail-never

COPY src src/

RUN ./mvnw install

EXPOSE 8080

ENTRYPOINT ["java","-jar","target/netcracker.jar"]

#ENV HOME=/usr/app
#
#RUN mkdir -p $HOME
#
#WORKDIR $HOME
#
#ADD pom.xml $HOME
#ADD mvnw $HOME
#ADD .mvn/ $HOME/.mvn
#
## get dependencies
#RUN ./mvnw verify --fail-never
#
#ADD . $HOME
## create the build, no tests
#RUN ./mvnw package -Dmaven.test.skip=true
#
#RUN ls -al target/
#RUN chmod 777 target/*
#COPY target/netcracker.jar application.jar
#
#RUN java -Djarmode=layertools -jar application.jar extract
#
#
#FROM eclipse-temurin:17
#
#ENV HOME=/usr/app
#
#COPY --from=builder $HOME/dependencies/ ./
#COPY --from=builder $HOME/snapshot-dependencies/ ./
#COPY --from=builder $HOME/spring-boot-loader/ ./
#COPY --from=builder $HOME/application/ ./
#
#ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]