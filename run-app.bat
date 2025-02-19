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
docker build --build-arg -t media-app:latest -f Dockerfile .
echo Build process completed.

cd ..
cd keycloak

echo Building Keycloak application...
docker build -t mykeycloak:latest -f Dockerfile .

cd ..

echo Starting Docker Compose...
start "docker-compose" docker-compose --env-file merged.env up --build

echo Waiting for Docker Compose to start services...

set backend_script_run=0
set music_app_script_run=0

:check_services
timeout /T 10

for /f "tokens=*" %%i in ('docker-compose ps -q backend') do (
    set backend_status=%%i
)
for /f "tokens=*" %%i in ('docker-compose ps -q music-app') do (
    set music_app_status=%%i
)

if not "%backend_status%"=="" (
    echo Backend service is up and running.
    if %backend_script_run%==0 (
        echo Running init_pets.bat script...
        call init_pets.bat
        set backend_script_run=1
    )
)

if not "%music_app_status%"=="" (
    echo Music App service is up and running.
    if %music_app_script_run%==0 (
        echo Running fix.bat script...
        call fix.bat
        set music_app_script_run=1
    )
)

if %backend_script_run%==1 if %music_app_script_run%==1 (
    echo All tasks completed. Exiting...
    pause
)

echo Waiting for services to be healthy...
goto check_services
