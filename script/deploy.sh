#!/bin/bash

ENV_FILE="./.env"


pushd ~/Downloads/botlearn/ || exit

docker compose -f docker-compose.yml --env-file $ENV_FILE down --timeout=60 --remove-orphans
docker compose -f docker-compose.yml --env-file $ENV_FILE up --build --detach
docker network create mynetwork

popd || exit