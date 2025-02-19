@echo off

REM Check if the network already exists
docker network inspect webapp_webnet >nul 2>&1

REM If the network does not exist, create it
if %errorlevel% neq 0 (
    docker network create --driver bridge --subnet 172.22.0.0/24 webapp_webnet
    docker network create --driver bridge appnet
	echo Network configuration complete.
) else (
    echo Network already exists.
)
