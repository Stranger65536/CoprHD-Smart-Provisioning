#!/usr/bin/env bash
curl localhost:8080/health
curl localhost:8080/test/mongo
curl localhost:8080/test/hz
curl localhost:8080/test/vipr
curl localhost:8080/test/srm