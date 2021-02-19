#!/usr/bin/env bash

docker stop bal-benchmark-cont 2>/dev/null
docker rm bal-benchmark-cont 2>/dev/null

docker run -e "BALLERINA_MAX_POOL_SIZE=${1}" --name bal-benchmark-cont --net="host" -p 9090:9090 --cpus="2" -d bal-benchmark