#!/usr/bin/env bash

docker stop jmeter-cont 2>/dev/null
docker rm jmeter-cont 2>/dev/null

curdir=$(pwd)
cd testcont/jmeter


# Removed deamon flag in order to run one by one
docker run -e "POOL_SIZE=${1}" --mount type=bind,source="$(pwd)"/tests,target=/usr/tests --network host --name jmeter-cont --cpus="2" jmeter

cd $curdir

