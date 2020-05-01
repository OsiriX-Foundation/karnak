Karnak is a DICOM Gateway with normalization and de-identification capabilities.

# Application Features

 - Allows a mapping between the AETitle of the DICOM Listener and the final DICOM or DICOMweb destination  
 - Allows multiples destinations (DICOM StoreSCU and/or STWO-RS output)
 - Filter the images providers by AETitle and/or hostname (Guarantees the authenticity of the source)


# Build Karnak

Prerequisites: JDK 14 and Maven 3

Execute the maven command `mvn clean install` in the root directory of the project.

# Run Karnak

To launch the UI, execute the maven command `mvn spring-boot:run -f mvc` in the root directory of the project.

The UI could be launch with a database in memory (H2), by using maven profiles :
 - devh2: `mvn -Pdevh2 spring-boot:run -f mvc`
 
## Debug in IntelliJ

 - Configure locally mainzelliste and Postgres database (see below)
 - Enable Sring and Spring Boot for the project
 - Create a Spring Boot launcher from main of SartApplication.java
    - Working Directory must be the mvc directory
    - Copy the KARNAK environment variables in docker/.env and paste into User environment variables  
    - In VM Options, add `-Djava.library.path="/tmp/dicom-opencv"`    
    Note: the tmp folder must be adapted according to your system and the dicom-opencv must the last folder.

## Debug in Eclipse

 - Configure locally mainzelliste and Postgres database (see below)
 - From Eclipse Marketplace: install the latest Spring Tools
 - Create a Spring Boot App launcher from main of SartApplication.java
    - Copy the KARNAK environment variables in docker/.env and paste into the Environment tab of the launcher    
    - In the Arguments tab of the launcher, add in VM arguments: `-Djava.library.path="/tmp/dicom-opencv"`    
    Note: the tmp folder must be adapted according to your system and the dicom-opencv must the last folder.

## Configure locally mainzelliste and Postgres database

- Go in the docker folder located in the root project folder
- Create a .env file and copy the content of the env.example
- Adapt the values if necessary
- Execute command:    
    - start: `docker-compose up -d`
    - stop: `docker-compose down`

# Docker

Build docker image:
 - with devh2: `mvn spring-boot:build-image -P devh2,production -f mvc`
 - with postgres: `mvn spring-boot:build-image -P production -f mvc`

Run Karnak: `docker run -it -p8081:8081 -p11119:11119 karnak-mvc:5.0.0-SNAPSHOT`

# Architecture

This project provides two modules:
 - karnak-data: the data model for persistence of the gateway configuration 
 - karnak-mvc: the services and UI for updating the data model

# Workflow

![Workflow](doc/karnak-workflow.svg)

# Pipeline

![Workflow](doc/karnak-pipeline.svg)
