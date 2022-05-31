# To build, run the following command from the top level project directory:
#
# docker build -t osirixfoundation/karnak:latest -f src/main/docker/Dockerfile .

RUN addgroup -S karnakgrp && adduser -S karnakuser -G karnakgrp -h /home/karnakuser
USER karnakuser

# Based on build image containing maven, jdk and git
#FROM maven:3.8-openjdk-17 as builder
FROM maven:3.6-adoptopenjdk-15 as builder
ARG JAR_FILE=target/karnak*.jar
WORKDIR /app
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# Build the final deployment image
#FROM maven:3.8-openjdk-17
FROM adoptopenjdk:15-jre-hotspot
WORKDIR app
COPY --from=builder /app/dependencies/ ./
RUN true
COPY --from=builder /app/spring-boot-loader/ ./
RUN true
COPY --from=builder /app/snapshot-dependencies/ ./
RUN true
COPY --from=builder /app/application/ ./
RUN true
COPY tools/docker-entrypoint.sh .

EXPOSE 8080
ENTRYPOINT ["/app/docker-entrypoint.sh"]