#!/usr/bin/env bash

source ./common.sh

export VERSION="$(version)"

./gradlew clean build

echo "Building version '${VERSION}'"

docker-compose build