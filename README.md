# Requirements

# How to run with docker
- 
```sh
docker-compose up
# PS: this is a self contained build that downloads all java+node dependencies and localstack to simulate s3
# this can take a pretty long time depending on internnet speed
```
- then goto localhost:8080

# How to run locally
- requires java 17, docker-compose
1. docker-compose up localstack
2. ./mvnw spring-boot:run
