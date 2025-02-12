@echo off
set /p key="Please enter the API key: "

REM Write the API key to the .env file
echo API_KEY=%key%> API_KEY.env

echo The API key has been set in the API_KEY.env file.
