#!/bin/bash

# Set default value for API_KEY
export API_KEY=${API_KEY:-rsa123}

echo "Running network.sh to create subnet..."
source network.sh

echo "Stopping running Docker Compose services..."
docker-compose down

echo "Building Java Spring Boot application..."
docker build -t backend:latest -f Dockerfile .

cd frontend

echo "Running environment setup script..."
node set-env.js

echo "Building Angular frontend application..."
docker build -t frontend:latest -f Dockerfile .

cd ..
cd integration-developer-master

echo "Building Music App application..."
docker build -t media-app:latest -f Dockerfile .
echo "Build process completed."

cd ..

echo "Starting Docker Compose..."
docker-compose --env-file API_KEY.env up --build
