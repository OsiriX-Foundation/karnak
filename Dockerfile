FROM alpine/git:1.0.14 as download-stage

WORKDIR /tmp

RUN git clone https://github.com/nroduit/dcm4che20.git
RUN git clone https://github.com/nroduit/weasis-dicom-tools.git
RUN (cd dcm4che20 && git checkout image)
RUN (cd weasis-dicom-tools && git checkout dcm4che6)

FROM maven:3.6-openjdk-14 as build-stage

WORKDIR /build

COPY . .
COPY --from=download-stage /tmp/dcm4che20 /dcm4che20
COPY --from=download-stage /tmp/weasis-dicom-tools /weasis-dicom-tools

RUN mvn com.github.eirslett:frontend-maven-plugin:1.7.6:install-node-and-npm -DnodeVersion="v12.14.0"
RUN (cd /dcm4che20 && mvn source:jar install)
RUN (cd /weasis-dicom-tools && mvn clean install)
RUN mvn clean package -Pproduction

FROM openjdk:14.0-jdk as production-stage

WORKDIR /app

COPY --from=build-stage /build/mvc/target/karnak-mvc-5.0.0-SNAPSHOT.jar /app/karnak-mvc-5.0.0-SNAPSHOT.jar
COPY tools/docker-entrypoint.sh .

EXPOSE 8088
ENTRYPOINT ["/app/docker-entrypoint.sh"]
