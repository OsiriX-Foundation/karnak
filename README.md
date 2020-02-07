Karnak is a DICOM Gateway with normalization and de-identification capabilities.

# Application Features

* Allows a mapping between the AETitle of the DICOM Listener and the final DICOM destination  
* Allows multiples destinations (DICOM StoreSCU and/or STWO-RS output)
* Filter the images providers by AETitle and/or hostname (Guarantees the authenticity of the source)


# Build Karnak

Prerequisites: JDK 11 and Maven 3

Execute the maven command `mvn clean install` in the root directory of the project.

# Run Karnak

To launch the UI, execute the maven command `mvn spring-boot:run -f mvc` in the root directory of the project.

The UI could be launch with a database in memory (H2), by using maven profiles :
 - devh2: `mvn -Pdevh2 spring-boot:run -f mvc`

# Architecture

This project provides two modules:
- karnak-data: the data model for persistence of the gateway configuration 
- karnak-mvc: the services and UI for updating the data model



