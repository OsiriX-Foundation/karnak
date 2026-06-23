# To build, run the following command from the top level project directory:
#
# docker build -t osirixfoundation/karnak:latest -f src/main/docker/Dockerfile .

# Based on build image containing maven, jdk and git
FROM maven:3-eclipse-temurin-26-noble AS builder
ARG JAR_FILE=target/karnak*.jar
WORKDIR /app
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# Build the final deployment image
FROM eclipse-temurin:25-jdk-noble
WORKDIR app

COPY --from=builder /app/extracted/dependencies/ ./
RUN true
COPY --from=builder /app/extracted/spring-boot-loader/ ./
RUN true
COPY --from=builder /app/extracted/snapshot-dependencies/ ./
RUN true
COPY --from=builder /app/extracted/application/ ./
RUN true
COPY tools/docker-entrypoint.sh .

EXPOSE 8080
ENTRYPOINT ["/app/docker-entrypoint.sh"]
