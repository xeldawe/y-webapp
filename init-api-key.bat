@echo off
set /p key="Please enter the API key: "

REM Write the API key to the .env file
echo API_KEY=%key%> api-key.env

echo The API key has been set in the api-key.env file.
