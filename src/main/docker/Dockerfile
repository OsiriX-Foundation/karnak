# To build, run the following command from the top level project directory:
#
# docker build -t osirixfoundation/karnak:latest -f src/main/docker/Dockerfile .

# Based on build image containing maven, jdk and git
FROM maven:3.8-eclipse-temurin-17 as builder
ARG JAR_FILE=target/karnak*.jar
WORKDIR /app
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# Build the final deployment image
FROM eclipse-temurin:17-jre-focal
WORKDIR app
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
COPY tools/docker-entrypoint.sh .

EXPOSE 8080
ENTRYPOINT ["/app/docker-entrypoint.sh"]