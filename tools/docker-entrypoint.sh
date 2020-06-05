#!/bin/bash

# https://github.com/keycloak/keycloak-containers/blob/master/server/tools/docker-entrypoint.sh#L4-L28
# usage: file_env VAR [DEFAULT]
#    ie: file_env 'XYZ_DB_PASSWORD' 'example'
# (will allow for "$XYZ_DB_PASSWORD_FILE" to fill in the value of
#  "$XYZ_DB_PASSWORD" from a file, especially for Docker's secrets feature)
file_env() {
    local var="$1"
    local fileVar="${var}_FILE"
    local def="${2:-}"
    if [[ ${!var:-} && ${!fileVar:-} ]]; then
        echo >&2 "error: both $var and $fileVar are set (but are exclusive)"
        exit 1
    fi
    local val="$def"
    if [[ ${!var:-} ]]; then
        val="${!var}"
    elif [[ ${!fileVar:-} ]]; then
        val="$(< "${!fileVar}")"
    fi

    if [[ -n $val ]]; then
        export "$var"="$val"
    fi

    unset "$fileVar"
}

SYS_PROPS=""

SYS_PROPS+=" -Djava.library.path='/tmp/dicom-opencv'"

########################
# DATABASE ENVIRONMENT #
########################
file_env 'DB_USER'
file_env 'DB_NAME'
file_env 'DB_PASSWORD'
: "${DB_USER:=karnak}"
: "${DB_PASSWORD:=karnak}"
: "${DB_NAME:=karnak}"
: "${DB_HOST:=localhost}"
: "${DB_PORT:=5432}"

SYS_PROPS+=" -Dkarnak.database.user=$DB_USER"
SYS_PROPS+=" -Dkarnak.database.password=$DB_PASSWORD"
SYS_PROPS+=" -Dkarnak.database.name=$DB_NAME"
SYS_PROPS+=" -Dkarnak.database.host=$DB_HOST"
SYS_PROPS+=" -Dkarnak.database.port=$DB_PORT"

############################
# MAINZELLISTE ENVIRONMENT #
############################
file_env 'MAINZELLISTE_API_KEY'
: "${MAINZELLISTE_HOSTNAME:=localhost}"
: "${MAINZELLISTE_HTTP_PORT:=8080}"
: "${MAINZELLISTE_ID_TYPES:=pid}"
: "${MAINZELLISTE_API_KEY:=undefined}"

SYS_PROPS+=" -Dkarnak.mainzelliste.hostname=$MAINZELLISTE_HOSTNAME"
SYS_PROPS+=" -Dkarnak.mainzelliste.httpPort=$MAINZELLISTE_HTTP_PORT"
SYS_PROPS+=" -Dkarnak.mainzelliste.idTypes=$MAINZELLISTE_ID_TYPES"
SYS_PROPS+=" -Dkarnak.mainzelliste.apiKey=$MAINZELLISTE_API_KEY"


eval java $SYS_PROPS -jar /app/karnak-mvc-5.0.0-SNAPSHOT.jar
# exec /opt/jboss/keycloak/bin/standalone.sh $SYS_PROPS $@