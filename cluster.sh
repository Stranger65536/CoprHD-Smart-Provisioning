#!/usr/bin/env bash

command="${1}"

function help {
    echo "Usage: cluster.sh <c,d> [12,23,13]"
}

if [ "${command}" != 'c' ] && [ "${command}" != 'd' ]; then
    echo "Command arguments is invalid: {$command}"
    help
    exit 1
fi

net="${2}"

if [ "${net}" == "" ]; then
    echo "Net argument is missing!"
    help
    exit 1
fi

if [ "${command}" == 'c' ]; then
    if [ "${net}" == "12" ]; then
        docker network connect --ip 172.48.11.4 coprhdsp_dc-12 node-1
        docker network connect --ip 172.48.11.2 coprhdsp_dc-12 hz-1
        docker network connect --ip 172.48.11.5 coprhdsp_dc-12 node-2
        docker network connect --ip 172.48.11.3 coprhdsp_dc-12 hz-2
    elif [ "${net}" == "23" ]; then
        docker network connect --ip 172.48.12.4 coprhdsp_dc-23 node-2
        docker network connect --ip 172.48.12.2 coprhdsp_dc-23 hz-2
        docker network connect --ip 172.48.12.5 coprhdsp_dc-23 node-3
        docker network connect --ip 172.48.12.3 coprhdsp_dc-23 hz-3
    elif [ "${net}" == "13" ]; then
        docker network connect --ip 172.48.13.4 coprhdsp_dc-13 node-1
        docker network connect --ip 172.48.13.2 coprhdsp_dc-13 hz-1
        docker network connect --ip 172.48.13.5 coprhdsp_dc-13 node-3
        docker network connect --ip 172.48.13.3 coprhdsp_dc-13 hz-3
    else
        echo "Invalid net! ${net}"
        help
    fi
fi

if [ "${command}" == 'd' ]; then
    if [ "${net}" == "12" ]; then
        docker network disconnect coprhdsp_dc-12 node-1
        docker network disconnect coprhdsp_dc-12 hz-1
        docker network disconnect coprhdsp_dc-12 node-2
        docker network disconnect coprhdsp_dc-12 hz-2
    elif [ "${net}" == "23" ]; then
        docker network disconnect coprhdsp_dc-23 node-2
        docker network disconnect coprhdsp_dc-23 hz-2
        docker network disconnect coprhdsp_dc-23 node-3
        docker network disconnect coprhdsp_dc-23 hz-3
    elif [ "${net}" == "13" ]; then
        docker network disconnect coprhdsp_dc-13 node-1
        docker network disconnect coprhdsp_dc-13 hz-1
        docker network disconnect coprhdsp_dc-13 node-3
        docker network disconnect coprhdsp_dc-13 hz-3
    else
        echo "Invalid net! ${net}"
        help
    fi
fi