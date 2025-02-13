@echo off

echo Running init-frontend.bat to set API key...
call init-api-key.bat

echo Running network.bat to create subnet...
call network.bat

echo Stopping running Docker Compose services...
docker-compose down

echo Merging the environment files into a single file...
call merge.bat

:: Read the merged.env file and set the variables
for /f "tokens=1,2 delims==" %%i in (merged.env) do (
    set %%i=%%j
)

echo Building Java Spring Boot application...
docker build --build-arg API_KEY=%API_KEY% --build-arg FILTER_INTERVAL=%FILTER_INTERVAL% --build-arg API_URL=%API_URL% -t backend:latest -f Dockerfile .

cd frontend

echo Building Angular frontend application...
docker build --build-arg API_KEY=%API_KEY% --build-arg FILTER_INTERVAL=%FILTER_INTERVAL% --build-arg API_URL=%API_URL% -t frontend:latest -f Dockerfile .

cd ..
cd integration-developer-master

echo Building Music App application...
docker build --build-arg API_KEY=%API_KEY% --build-arg FILTER_INTERVAL=%FILTER_INTERVAL% --build-arg API_URL=%API_URL% -t media-app:latest -f Dockerfile .
echo Build process completed.

cd ..

echo Starting Docker Compose...
docker-compose --env-file merged.env up --build

pause
