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

echo "Create keycloak_data..."
docker volume create keycloak_data

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
cd keycloak

echo "Building Keycloak application..."
docker build -t mykeycloak:latest -f Dockerfile .

cd ..

echo "Starting Docker Compose..."
docker-compose --env-file merged.env up --build &

# Wait for Docker Compose to finish starting services
echo "Waiting for the application services to start..."

backend_script_run=0
music_app_script_run=0

# Check the status of services
while [[ $backend_script_run -eq 0 || $music_app_script_run -eq 0 ]]
do
  if [[ $backend_script_run -eq 0 ]] && docker-compose ps | grep 'backend' | grep 'Up'; then
    echo "Backend service is up and running."
    echo "Running init_pets.sh script..."
    ./init_pets.sh
    backend_script_run=1
  fi

  if [[ $music_app_script_run -eq 0 ]] && docker-compose ps | grep 'music-app' | grep 'Up'; then
    echo "Music App service is up and running."
    echo "Running fix.sh script..."
    ./fix.sh
    music_app_script_run=1
  fi

  if [[ $backend_script_run -eq 0 || $music_app_script_run -eq 0 ]]; then
    echo "Waiting for services to be healthy..."
    sleep 10
  fi
done

# Exit the script when done
echo "All tasks completed. Exiting..."
exit