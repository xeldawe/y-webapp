@echo off
setlocal

:: Volume name and backup file name
set VOLUME_NAME=keycloak_data
set BACKUP_FILE=%cd%\keycloak_data_backup.tar.gz

:: Check if the volume exists, create a backup if it does
echo Checking if the volume exists...
docker volume inspect %VOLUME_NAME% >nul 2>&1
if %errorlevel% neq 0 (
    echo Volume does not exist. Creating...
    docker volume create %VOLUME_NAME%
    echo Volume created: %VOLUME_NAME%
) else (
    echo Volume exists. Creating backup...
    docker run --rm -v %VOLUME_NAME%:/data -v %cd%:/backup alpine tar czf /backup/keycloak_data_backup.tar.gz -C /data .
    echo Backup created: %BACKUP_FILE%
)

:: Restore the volume from the backup
echo Restoring...
docker volume inspect %VOLUME_NAME% >nul 2>&1
if %errorlevel% neq 0 (
    echo Volume does not exist. Creating...
    docker volume create %VOLUME_NAME%
    echo Volume created: %VOLUME_NAME%
)

echo Restoring from backup file...
docker run --rm -v %VOLUME_NAME%:/data -v %cd%:/backup alpine sh -c "tar xzf /backup/keycloak_data_backup.tar.gz -C /data"
echo Restore completed.

endlocal
echo Operation completed.
pause
