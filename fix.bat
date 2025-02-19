@echo off
setlocal enabledelayedexpansion

REM Define variables
set MYSQL_CONTAINER=y-webapp-mysql-1
set MYSQL_USER=test
set MYSQL_PASSWORD=testpw
set SQL_COMMANDS_FILE=integration-developer-master\db\init.sql

:retry
REM Drop the existing music database
echo Dropping the existing music database...
docker exec -i %MYSQL_CONTAINER% mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% -e "DROP DATABASE IF EXISTS music;"
if errorlevel 1 (
    echo "Failed to drop database. Retrying..."
    timeout /T 10
    goto retry
)

REM Recreate the music database
echo Recreating the music database...
docker exec -i %MYSQL_CONTAINER% mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% -e "CREATE DATABASE music;"
if errorlevel 1 (
    echo "Failed to create database. Retrying..."
    timeout /T 10
    goto retry
)

REM Run the SQL commands from the file
echo Running init.sql to set up the schema and seed the database...
docker exec -i %MYSQL_CONTAINER% mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% music < %SQL_COMMANDS_FILE%
if errorlevel 1 (
    echo "Failed to run init.sql. Retrying..."
    timeout /T 10
    goto retry
)

echo "Database setup completed successfully."
