@echo off
setlocal enabledelayedexpansion

:: Check if global.env and API_KEY.env have content
if not exist global.env (
    echo global.env is empty or does not exist.
    exit /b 1
)

if not exist API_KEY.env (
    echo API_KEY.env is empty or does not exist.
    exit /b 1
)

:: Display the content of global.env
echo Content of global.env:
type global.env

:: Display the content of API_KEY.env
echo Content of API_KEY.env:
type API_KEY.env

:: Merge the environment files into a single file
(
    type global.env
    echo.
    type API_KEY.env
) > merged.env

:: Check if merged.env was created successfully and has content
if not exist merged.env (
    echo Failed to merge environment files or merged.env is empty.
    exit /b 1
)

:: Display the content of merged.env
echo Content of merged.env:
type merged.env
