#!/usr/bin/env bash

source ./common.sh

export VERSION="${1}"

if [ "${VERSION}" == "" ]; then
    export VERSION="$(version)"
fi

echo "Running version '${VERSION}'"

docker-compose -p CoprHDSP up -d