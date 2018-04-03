#!/usr/bin/env bash

source ./common.sh

export VERSION="$(version)"

echo "Building version '${VERSION}'"

docker-compose build