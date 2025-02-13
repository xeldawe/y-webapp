@echo off
REM Define variables
set MYSQL_CONTAINER=webapp-mysql-1
set MYSQL_USER=test
set MYSQL_PASSWORD=testpw

REM SQL file content with all the commands
set SQL_COMMANDS_FILE=integration-developer-master\db\init.sql

REM Drop the existing music database
echo Dropping the existing music database...
docker exec -i %MYSQL_CONTAINER% mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% -e "DROP DATABASE IF EXISTS music;"
if errorlevel 1 (
    echo Failed to drop database. Aborting script.
    pause
    exit /b 1
)


REM Recreate the music database
echo Recreating the music database...
docker exec -i %MYSQL_CONTAINER% mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% -e "CREATE DATABASE music;"
if errorlevel 1 (
    echo Failed to create database. Aborting script.
    pause
    exit /b 1
)


REM Run the SQL commands from the file
echo Running init.sql to set up the schema and seed the database...
docker exec -i %MYSQL_CONTAINER% mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% music < %SQL_COMMANDS_FILE%
if errorlevel 1 (
    echo Failed to run init.sql. Aborting script.
    pause
    exit /b 1
)


echo Done!
pause
