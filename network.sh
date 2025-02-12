#!/bin/bash

# Check if the network already exists
if docker network inspect webapp_webnet >/dev/null 2>&1; then
    echo "Network already exists."
else
    # If the network does not exist, create it
    docker network create --driver bridge --subnet 172.22.0.0/24 webapp_webnet
    echo "Network configuration complete."
fi
