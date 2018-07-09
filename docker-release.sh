#!/usr/bin/env bash

#----------------------- Release testing build -------------------------------
IMAGE_NAME="dummy-smtp-service"
DOCKER_TAG="dummy-smtp-service"
DOCKER_REPO=""

docker rmi -f $IMAGE_NAME

echo "build tar file"
./gradlew clean copyConfig distTar

echo "build docker image"
docker build -t $IMAGE_NAME .

echo "build docker image"
docker tag $IMAGE_NAME:latest $DOCKER_REPO:$DOCKER_TAG

echo "Publish image"
docker push $DOCKER_REPO:$DOCKER_TAG
#------------------------------------------------------------------------------