FROM openjdk:14.0-jdk

EXPOSE 8088

COPY mvc/target/karnak-mvc-5.0.0-SNAPSHOT.jar .

CMD java -Djava.library.path="/tmp/dicom-opencv" -jar karnak-mvc-5.0.0-SNAPSHOT.jar