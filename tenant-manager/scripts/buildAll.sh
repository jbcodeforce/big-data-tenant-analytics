#!/bin/bash
scriptDir=$(dirname $0)

IMAGE_NAME=jbcodeforce/demo-saas-tenant-mgr

if [[ $# -eq 2 ]]
then
  REPO=$1
  TAG=$2
else
  TAG=latest
  REPO=
fi

./mvnw clean package  -DskipTests
docker build -f src/main/docker/Dockerfile.jvm -t  ${IMAGE_NAME}:${TAG} .
docker tag  ${IMAGE_NAME}:${TAG}   ${REPO}/${IMAGE_NAME}:${TAG}
docker push ${REPO}/${IMAGE_NAME}:${TAG}
