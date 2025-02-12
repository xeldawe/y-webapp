@echo off

echo Running init-frontend.bat to set API key...
call init-api-key.bat

echo Running network.bat to create subnet...
call network.bat

echo Stopping running Docker Compose services...
docker-compose down

echo Building Java Spring Boot application...
docker build -t backend:latest -f Dockerfile .

cd frontend

echo Building Angular frontend application...
docker build -t frontend:latest -f Dockerfile .

cd..
cd integration-developer-master

echo Building Music App application...
docker build -t media-app:latest -f Dockerfile .
echo Build process completed.

cd ..

echo Starting Docker Compose...
docker-compose --env-file API_KEY.env up --build

pause
