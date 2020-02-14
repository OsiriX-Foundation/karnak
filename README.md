Karnak is a DICOM Gateway with normalization and de-identification capabilities.

# Application Features

 - Allows a mapping between the AETitle of the DICOM Listener and the final DICOM destination  
 - Allows multiples destinations (DICOM StoreSCU and/or STWO-RS output)
 - Filter the images providers by AETitle and/or hostname (Guarantees the authenticity of the source)


# Build Karnak

Prerequisites: JDK 11 and Maven 3

Execute the maven command `mvn clean install` in the root directory of the project.

# Run Karnak

To launch the UI, execute the maven command `mvn spring-boot:run -f mvc` in the root directory of the project.

The UI could be launch with a database in memory (H2), by using maven profiles :
 - devh2: `mvn -Pdevh2 spring-boot:run -f mvc`
 
## Debug in IntelliJ
 - Enable Sring and Spring Boot for the project
 - Create a Spring Boot from main of SartApplication.java
 - Working Directory must be the mvc directory
 
## Debug in Eclipse
 - From Eclipse Marketplace: install the latest Spring Tools
 - To support devh2 profile dependencies add the profile name the the karnak-mvc project with the contextual menu: Properties -> Maven-> Active Maven Profiles
 - Create a Spring Boot App launcher from main of SartApplication.java

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
