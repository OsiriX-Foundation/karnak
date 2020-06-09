Karnak is a DICOM Gateway with normalization and de-identification capabilities.

# Application Features

 - Allows a mapping between the AETitle of the DICOM Listener and the final DICOM destination  
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
    - In Environment variables, add the following values. 
    The following values work with our default configuration define with docker used for the development (see: "Configure locally mainzelliste and Postgres database with docker-compose") :
        - `DB_PASSWORD=5!KAnN@%98%d`
        - `DB_PORT=5433`
        - `DB_USER=karnak`
        - `DB_NAME=karnak`
        - `DB_HOST=localhost`
        - `MAINZELLISTE_HOSTNAME=localhost`
        - `MAINZELLISTE_HTTP_PORT=8083`
        - `MAINZELLISTE_ID_TYPES=pid`
        - `MAINZELLISTE_API_KEY=changeThisApiKey`
        - `KARNAK_HMAC_KEY=changeThisHmacKey`

    Note: the tmp folder must be adapted according to your system and the dicom-opencv must the last folder.

## Debug in Eclipse

 - Configure locally mainzelliste and Postgres database (see below)
 - From Eclipse Marketplace: install the latest Spring Tools
 - Create a Spring Boot App launcher from main of SartApplication.java
    - Copy the KARNAK environment variables in docker/.env and paste into the Environment tab of the launcher    
    - In the Arguments tab of the launcher, add in VM arguments: `-Djava.library.path="/tmp/dicom-opencv"`    
    Note: the tmp folder must be adapted according to your system and the dicom-opencv must the last folder.

## Configure locally mainzelliste and Postgres database with docker-compose

Minimum docker-compose version: **1.22**

- Go in the `docker/dev` folder located in the root project folder.
- To configure the docker used by karnak, please refer to this links.
    - [docker hub postgres](https://hub.docker.com/_/postgres)
    - [docker hub mainzelliste](https://hub.docker.com/repository/docker/osirixfoundation/karnak-mainzelliste)
- Adapt the values if necessary
- Execute command:
    - start: `docker-compose up -d`
    - show the logs: `docker-compose logs -f`
    - stop: `docker-compose down`

# Docker

Minimum docker version: **19.03**

## Build with Dockerfile

Go on the root folder and launch the following command:

`docker build -t karnak/locally:latest .`

Run Karnak: `docker run -it -p8081:8081 -p11119:11119 karnak/locally:latest`

## Build with mvn spring-boot

 - with devh2: `mvn spring-boot:build-image -P devh2,production -f mvc`
 - with postgres: `mvn spring-boot:build-image -P production -f mvc`

Run Karnak: `docker run -it -p8081:8081 -p11119:11119 karnak-mvc:5.0.0-SNAPSHOT`

## Docker environment variables

`DB_USER`

User of the karnak database (optional, default is `karnak`).

`DB_USER_FILE`

User of the karnak database via file input (alternative to `DB_USER`).

`DB_PASSWORD`

Password of the karnak database (optional, default is `karnak`).

`DB_PASSWORD_FILE`

Password of the karnak database via file input (alternative to `DB_PASSWORD`).

`DB_NAME`

Name of the karnak database (optional, default is `karnak`).

`DB_NAME_FILE`

Name of the karnak database via file input (alternative to `DB_NAME`).

`DB_HOST`

Hostname/IP Address of the PostgreSQL host. (optional, default is `localhost`).

`DB_PORT`

Port of the PostgreSQL host (optional, default is `5432`)

`MAINZELLISTE_HOSTNAME`

Hostname/IP Address of the Mainzelliste host. (optional, default is `localhost`).

`MAINZELLISTE_HTTP_PORT`

Port of the Mainzelliste host. (optional, default is `8080`).

`MAINZELLISTE_ID_TYPES`

Type of pseudonym to be created and sent.

`MAINZELLISTE_API_KEY`

The api key used to connect to Mainzelliste host (optional, default is `undefined`)

`KARNAK_HMAC_KEY`

The key used for the HMAC. This HMAC will be used for all the hash created by karnak

`KARNAK_HMAC_KEY_FILE`

The key used for the HMAC via file input. (alternative to `KARNAK_HMAC_KEY`).

# Architecture

This project provides two modules:
 - karnak-data: the data model for persistence of the gateway configuration 
 - karnak-mvc: the services and UI for updating the data model

# Workflow

![Workflow](doc/karnak-workflow.svg)

# Pipeline

![Workflow](doc/karnak-pipeline.svg)
