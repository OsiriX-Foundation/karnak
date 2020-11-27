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

# https://github.com/dcm4che-dockerfiles/wildfly/blob/1550dced41da57248c40b963706e856c67d31858/docker-entrypoint.sh#L45
# for c in $KARNAK_WAIT_FOR; do
#   echo "Waiting for $c ..."
#    while ! nc -w 1 -z ${c/:/ }; do sleep 1; done
#     echo "done"
# done

SYS_PROPS=""

SYS_PROPS+=" -Djava.library.path='/tmp/dicom-opencv'"

########################
# KARNAK ENVIRONMENT #
########################
file_env 'KARNAK_LOGIN_PASSWORD'
: "${KARNAK_LOGIN_PASSWORD:=undefined}"
SYS_PROPS+=" -Dkarnakadmin='$KARNAK_LOGIN_ADMIN'"
SYS_PROPS+=" -Dkarnakpassword='$KARNAK_LOGIN_PASSWORD'"

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

DB_URL=jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME

SYS_PROPS+=" -Dspring.datasource.username=$DB_USER"
SYS_PROPS+=" -Dspring.datasource.password=$DB_PASSWORD"
SYS_PROPS+=" -Dspring.datasource.url=$DB_URL"

############################
# MAINZELLISTE ENVIRONMENT #
############################
file_env 'MAINZELLISTE_API_KEY'
: "${MAINZELLISTE_HOSTNAME:=localhost}"
: "${MAINZELLISTE_HTTP_PORT:=8080}"
: "${MAINZELLISTE_ID_TYPES:=pid}"
: "${MAINZELLISTE_API_KEY:=undefined}"

MAINZELLISTE_SERVER_URL=http://$MAINZELLISTE_HOSTNAME:$MAINZELLISTE_HTTP_PORT

SYS_PROPS+=" -Dmainzelliste.serverurl=$MAINZELLISTE_SERVER_URL"
SYS_PROPS+=" -Dmainzelliste.idtypes=$MAINZELLISTE_ID_TYPES"
SYS_PROPS+=" -Dmainzelliste.apikey=$MAINZELLISTE_API_KEY"

# https://docs.hazelcast.org/docs/4.1/manual/html-single/index.html#running-in-modular-java
# Hazelcast needs the java.se module and access to the following Java packages for a proper work
SYS_PROPS+=" --add-modules java.se --add-exports java.base/jdk.internal.ref=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.management/sun.management=ALL-UNNAMED --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED"

eval java $SYS_PROPS -jar /app/karnak-mvc-5.0.0-SNAPSHOT.jar
