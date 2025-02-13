@echo off

echo Running network.bat to create subnet...
call network.bat
REM Load Docker images from .tar files in the /docker directory
docker load -i docker\webapp_music-app.tar
docker load -i docker\webapp_postgres.tar
docker load -i docker\webapp_swagger-ui.tar
docker load -i docker\webapp_redis.tar
docker load -i docker\webapp_mysql.tar
docker load -i docker\webapp_backend.tar
docker load -i docker\webapp_frontend.tar
docker load -i docker\webapp_nginx.tar

REM Run Docker Compose
docker-compose up --build

pause
