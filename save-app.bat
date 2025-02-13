@echo off

REM Save Docker images to .tar files in the /docker directory
docker save -o C:\Users\Xel\Desktop\Project\webapp\docker\webapp_music-app.tar webapp-music-app
docker save -o C:\Users\Xel\Desktop\Project\webapp\docker\webapp_postgres.tar postgres:latest
docker save -o C:\Users\Xel\Desktop\Project\webapp\docker\webapp_swagger-ui.tar swaggerapi/swagger-ui
docker save -o C:\Users\Xel\Desktop\Project\webapp\docker\webapp_redis.tar redis:latest
docker save -o C:\Users\Xel\Desktop\Project\webapp\docker\webapp_mysql.tar mysql:latest
docker save -o C:\Users\Xel\Desktop\Project\webapp\docker\webapp_backend.tar webapp-backend
docker save -o C:\Users\Xel\Desktop\Project\webapp\docker\webapp_frontend.tar webapp-frontend
docker save -o C:\Users\Xel\Desktop\Project\webapp\docker\webapp_nginx.tar nginx:latest

pause
