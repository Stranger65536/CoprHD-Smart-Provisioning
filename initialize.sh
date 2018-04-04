#!/usr/bin/env bash

source ./common.sh

export VERSION="${1}"

if [ "${VERSION}" == "" ]; then
    export VERSION="$(version)"
fi

echo "Initializing version '${VERSION}'"

docker-compose -p CoprHDSP up -d

echo "Waiting mongo to start..."

sleep "5" #TODO mongo wait to start

docker exec -it mongo-node-1 mongo admin --eval "db.createCollection('virtual-pools')"
docker exec -it mongo-node-1 mongo admin --eval "db.createUser({ user: 'coprhd', pwd: 'password', roles: ['userAdminAnyDatabase', 'dbAdminAnyDatabase', 'readWriteAnyDatabase']});"
