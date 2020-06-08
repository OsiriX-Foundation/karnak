#!/bin/bash

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
file_env 'MAINZELLISTE_PID_K1'
file_env 'MAINZELLISTE_PID_K2'
file_env 'MAINZELLISTE_PID_K3'
: "${MAINZELLISTE_PID_K1:=undefined1}"
: "${MAINZELLISTE_PID_K2:=undefined2}"
: "${MAINZELLISTE_PID_K3:=undefined3}"

sed -e "s|idgenerator.pid.k1 = 1|idgenerator.pid.k1 = ${MAINZELLISTE_PID_K1}|g" /mainzelliste.conf.default
sed -e "s|idgenerator.pid.k2 = 2|idgenerator.pid.k2 = ${MAINZELLISTE_PID_K2}|g" /mainzelliste.conf.default
sed -e "s|idgenerator.pid.k3 = 3|idgenerator.pid.k3 = ${MAINZELLISTE_PID_K3}|g" /mainzelliste.conf.default

exec /ml_entrypoint.sh