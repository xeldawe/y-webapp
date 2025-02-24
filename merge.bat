@echo off
setlocal enabledelayedexpansion

:: Check if global.env and api-key.env have content
if not exist global.env (
    echo global.env is empty or does not exist.
    exit /b 1
)

if not exist api-key.env (
    echo api-key.env is empty or does not exist.
    exit /b 1
)

:: Display the content of global.env
echo Content of global.env:
type global.env

:: Display the content of api-key.env
echo Content of api-key.env:
type api-key.env

:: Merge the environment files into a single file
(
    type global.env
    echo.
    type api-key.env
) > merged.env

:: Check if merged.env was created successfully and has content
if not exist merged.env (
    echo Failed to merge environment files or merged.env is empty.
    exit /b 1
)

:: Display the content of merged.env
echo Content of merged.env:
type merged.env
