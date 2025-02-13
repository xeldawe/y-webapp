#!/bin/bash

echo "Running network.sh to create subnet..."
./network.sh

# Load Docker images from .tar files in the /docker directory
docker load -i /docker/webapp_music-app.tar
docker load -i /docker/webapp_postgres.tar
docker load -i /docker/webapp_swagger-ui.tar
docker load -i /docker/webapp_redis.tar
docker load -i /docker/webapp_mysql.tar
docker load -i /docker/webapp_backend.tar
docker load -i /docker/webapp_frontend.tar
docker load -i /docker/webapp_nginx.tar

# Run Docker Compose
docker-compose up

