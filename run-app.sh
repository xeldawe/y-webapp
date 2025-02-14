#!/bin/bash

# Merge the environment files into a single file
echo "Running merge.sh to merge environment files..."
source merge.sh

# Source the merged.env file to export variables
set -a
source merged.env
set +a

# Set default value for API_KEY if it's not set
export API_KEY=${API_KEY:-rsa123}

echo "Running network.sh to create subnet..."
source network.sh

echo "Stopping running Docker Compose services..."
docker-compose down

echo "Building Java Spring Boot application..."
docker build --build-arg API_KEY=$API_KEY --build-arg FILTER_INTERVAL=$FILTER_INTERVAL --build-arg API_URL=$API_URL -t backend:latest -f Dockerfile .

cd frontend

echo "Running environment setup script..."
# Ensure the environment variable is set
if [ -z "$API_KEY" ]; then
  export API_KEY="rsa123"
fi
node set-env.js

echo "Building Angular frontend application..."
# Pass the environment variables during the build
docker build --build-arg API_KEY=$API_KEY --build-arg FILTER_INTERVAL=$FILTER_INTERVAL --build-arg API_URL=$API_URL -t frontend:latest -f Dockerfile .

cd ..
cd integration-developer-master

echo "Building Music App application..."
docker build -t media-app:latest -f Dockerfile .
echo "Build process completed."

cd ..

echo "Starting Docker Compose..."
docker-compose --env-file merged.env up --build
