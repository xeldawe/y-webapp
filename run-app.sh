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
# Ensure the environment variable is set
if [ -z "$API_KEY" ]; then
  export API_KEY="rsa123"
fi
node set-env.js

echo "Building Angular frontend application..."
# Pass the environment variable during the build
docker build --build-arg API_KEY=$API_KEY -t frontend:latest -f Dockerfile .

cd ..
cd integration-developer-master

echo "Building Music App application..."
docker build -t media-app:latest -f Dockerfile .
echo "Build process completed."

cd ..

echo "Starting Docker Compose..."
docker-compose --env-file API_KEY.env up --build
