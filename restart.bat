@echo off

echo Stopping running Docker Compose services...
docker-compose down

echo Starting Docker Compose...
docker-compose up 
pause

