[![License](https://img.shields.io/badge/License-EPL%202.0-blue.svg)](https://opensource.org/licenses/EPL-2.0) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)  ![Maven build](https://github.com/OsiriX-Foundation/karnak/workflows/Build/badge.svg)

[![Sonar](https://sonarcloud.io/api/project_badges/measure?project=karnak&metric=ncloc)](https://sonarcloud.io/component_measures?id=karnak) [![Sonar](https://sonarcloud.io/api/project_badges/measure?project=karnak&metric=reliability_rating)](https://sonarcloud.io/component_measures?id=karnak) [![Sonar](https://sonarcloud.io/api/project_badges/measure?project=karnak&metric=sqale_rating)](https://sonarcloud.io/component_measures?id=karnak) [![Sonar](https://sonarcloud.io/api/project_badges/measure?project=karnak&metric=security_rating)](https://sonarcloud.io/component_measures?id=karnak) [![Sonar](https://sonarcloud.io/api/project_badges/measure?project=karnak&metric=alert_status)](https://sonarcloud.io/dashboard?id=karnak)

Karnak is a DICOM gateway designed for data de-identification and DICOM attribute normalization. It manages continuous DICOM data streams, functioning as a DICOM listener for input and supporting both DICOM and DICOMWeb formats for output.

For detailed usage instructions, refer to the [Karnak User Guide](https://osirix-foundation.github.io/karnak-documentation/).


# Application Features

## Gateway

- Allow to have multiple destinations for one source. 
- A destination can be DICOM or DICOMWeb.
- Provides filtering options for image providers by AE Title and/or hostname to ensure source authenticity.
- Provides filtering options for SOP Class UID to send only specific SOP Class UIDs.
- Use expressions to filter images from the DICOM tag values.

## De-identification

- Each destination can be configured with a specific de-identification profile
- [Build your own de-identification profile](https://osirix-foundation.github.io/karnak-documentation/docs/deidentification/profiles)
- Import and export the de-identification profiles to share them with other users.

# Build Karnak

Prerequisites:
- JDK 21
- Maven 3

##### Karnak

Execute the maven command `mvn clean install -P production` in the root directory of the project.

Note: When the tests are not skipped, it requires to run locally the cache, see below [Run locally the database and the cache with docker](#run-locally-the-database-and-the-cache-with-docker).

# Run Karnak

To configure and run Karnak with docker, see [karnak-docker](https://github.com/OsiriX-Foundation/karnak-docker).

# Debug Karnak

## Debug in IntelliJ

- Launch the components needed by Karnak (see below "Configure Postgres database with docker")
- Enable Spring and Spring Boot for the project
- Create a Spring Boot launcher from main of StartApplication.java
    - Working Directory must be the mvc directory
    - In VM Options:
      - Add `-Djava.library.path="/tmp/dicom-opencv"`. Note: the tmp folder must be adapted according to your system and `dicom-opencv` is mandatory as the last folder.
      - Optional: Add `-Dvaadin.productionMode=true` to enable production mode
    - In Environment variables, add the following values. The following values work with our default
      configuration define with docker used for the development (see: "Configure locally Postgres database with docker") :
        - Mandatory:
            - `ENVIRONMENT=DEV`
        - Optional:
            - `DB_PASSWORD=5!KAnN@%98%d`
            - `DB_PORT=5433`
            - `DB_USER=karnak`
            - `DB_NAME=karnak`
            - `DB_HOST=localhost`
            - `KARNAK_ADMIN=admin`
            - `KARNAK_PASSWORD=admin`
            - `KARNAK_LOGS_MAX_FILE_SIZE=100MB`
            - `KARNAK_LOGS_MIN_INDEX=1`
            - `KARNAK_LOGS_MAX_INDEX=10`
            - `KARNAK_CLINICAL_LOGS_MAX_FILE_SIZE=100MB`
            - `KARNAK_CLINICAL_LOGS_MIN_INDEX=1`
            - `KARNAK_CLINICAL_LOGS_MAX_INDEX=10`
            - `IDP=undefined`
            - `OIDC_CLIENT_ID=undefined`
            - `OIDC_CLIENT_SECRET=undefined`
            - `OIDC_ISSUER_URI=undefined`
            
## Run locally the database and the cache with docker

- Go in the `docker` folder located in the root project folder.
- To configure third-party components used by karnak, please refer to these links:
    - [docker hub postgres](https://hub.docker.com/_/postgres)
- Adapt the values if necessary (copy `.env.example` into `.env` and modify it)
- Execute command:
    - start: `docker compose up -d`
    - show the logs: `docker compose logs -f`
    - stop: `docker compose down`

# Docker

Minimum docker version: **20.10**

## Build with Dockerfile

Go on the root folder and launch the following command:

* Full independent build: `docker build -t local/karnak:latest -f Dockerfile .`
* Build from compile package:
    * `mvn clean install -P production`
    * `docker build -t local/karnak:latest -f src/main/docker/Dockerfile .`

## Run image from Docker Hub

See [karnak-docker](https://github.com/OsiriX-Foundation/karnak-docker)

## Docker environment variables

See [all the environment variables](https://github.com/OsiriX-Foundation/karnak-docker#environment-variables)

# Architecture

This project is divided in two parts:

- backend: spring data (entities, repositories, converters, validators), enums, 
        spring configurations, spring security, cache, spring services, models...
- frontend : Vaadin components:  logic services, graphic components, views

# Identity provider

An OpenID Connect identity provider can be configured by using the environment variables:
 - `IDP`:  when this environment variable has the value 'oidc', the following environment 
 variables will configure the OpenID Connect identity provider. Any other value will load the in 
 memory user configuration. 
 - `OIDC_CLIENT_ID`: client id of the identity provider 
 - `OIDC_CLIENT_SECRET`: client secret of the identity provider
 - `OIDC_ISSUER_URI`: issuer URI of the identity provider
 
# Documentation for API/Endpoints 

# Workflow

![Workflow](doc/karnak-workflow.svg)

# Pipeline

![Workflow](doc/karnak-pipeline.svg)
