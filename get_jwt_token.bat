@echo off
setlocal enabledelayedexpansion

:: Keycloak server details
set KEYCLOAK_URL=http://localhost:8082/realms/testrealm/protocol/openid-connect/token
set CLIENT_ID=testclient
set CLIENT_SECRET=n255vtdaUfWwNPoW79iBraKb0v2sGWh0
set USERNAME=xeldawe@gmail.com
set PASSWORD=testpw

:: Output files for the tokens
set TOKEN_FILE=jwt_token.txt

:: Delete existing token files if they exist
if exist %TOKEN_FILE% del %TOKEN_FILE%

:: Request the tokens using curl with --insecure option
echo Sending request to Keycloak...
curl --location --request POST "%KEYCLOAK_URL%" ^
--header "Content-Type: application/x-www-form-urlencoded" ^
--data-urlencode "client_id=%CLIENT_ID%" ^
--data-urlencode "client_secret=%CLIENT_SECRET%" ^
--data-urlencode "grant_type=password" ^
--data-urlencode "username=%USERNAME%" ^
--data-urlencode "password=%PASSWORD%" ^
--silent --insecure --output temp_response.json

:: Check if the temp_response.json file exists
if not exist temp_response.json (
    echo Error: temp_response.json file not found.
    goto cleanup
)

:: Display the response for debugging
echo Response from Keycloak:
type temp_response.json

:: Read the response into a variable and remove newline characters
set "response="
for /f "delims=" %%i in (temp_response.json) do set "response=!response!%%i"

:: Save the modified response to the output file
echo !response! > %TOKEN_FILE%

:: Display the saved response
echo JWT token saved to %TOKEN_FILE%:
type %TOKEN_FILE%

:cleanup
:: Clean up temporary files
if exist temp_response.json del temp_response.json

endlocal