# To build, run the following command from the top level project directory:
#
# docker build -t osirixfoundation/karnak:latest -f Dockerfile .

# Based on build image containing maven, jdk and git
FROM maven:3.6-adoptopenjdk-15 as builder
WORKDIR /app

# Build third-party libraries
RUN git clone --depth 1 https://github.com/nroduit/dcm4che20.git --single-branch --branch image
RUN mvn -B -f dcm4che20/pom.xml install
RUN git clone --depth 1 https://github.com/nroduit/weasis-dicom-tools.git --single-branch --branch dcm4che6
RUN mvn -B -f weasis-dicom-tools/pom.xml install

# Build the Spring Boot application with layers
COPY pom.xml .
COPY src ./src
COPY frontend frontend
COPY parent ./parent
RUN mvn -B package -P production
WORKDIR /app/bin
RUN cp ../target/karnak*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# Build the final deployment image
FROM adoptopenjdk:15-jre-hotspot
WORKDIR app
COPY --from=builder /app/bin/dependencies/ ./
COPY --from=builder /app/bin/spring-boot-loader/ ./
COPY --from=builder /app/bin/snapshot-dependencies/ ./
COPY --from=builder /app/bin/application/ ./
COPY tools/docker-entrypoint.sh .

EXPOSE 8080
ENTRYPOINT ["/app/docker-entrypoint.sh"]
