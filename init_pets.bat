@echo off
setlocal enabledelayedexpansion

:: Load the URL and port from the global.env file
for /f "tokens=1,2 delims==" %%i in (global.env) do (
    if "%%i"=="API_URL" set "API_URL=%%j"
)

:: Number of pets to insert
set numPets=10

:: Generate random data for pets
set names[0]=Buddy
set names[1]=Max
set names[2]=Bella
set names[3]=Charlie
set names[4]=Lucy
set names[5]=Daisy
set names[6]=Molly
set names[7]=Bailey
set names[8]=Lola
set names[9]=Stella

set tags[0]=tag1
set tags[1]=tag2
set tags[2]=tag3
set tags[3]=tag4
set tags[4]=tag5

set categories[0]=category1
set categories[1]=category2
set categories[2]=category3
set categories[3]=category4
set categories[4]=category5

:: Create JSON payload
set payload=[
for /L %%i in (1,1,%numPets%) do (
    :: Generate random indexes for names, tags, and categories
    set /a "nameIdx=!random! %% 10"
    set /a "tagIdx1=!random! %% 5"
    set /a "tagIdx2=!random! %% 5"
    set /a "categoryIdx1=!random! %% 5"
    set /a "categoryIdx2=!random! %% 5"
    
    :: Use the name from the names array
    for %%j in (!nameIdx!) do set "petName=!names[%%j]!"
    for %%j in (!tagIdx1!) do set "tagName1=!tags[%%j]!"
    for %%j in (!tagIdx2!) do set "tagName2=!tags[%%j]!"
    for %%j in (!categoryIdx1!) do set "categoryName1=!categories[%%j]!"
    for %%j in (!categoryIdx2!) do set "categoryName2=!categories[%%j]!"

    :: Construct JSON for this pet
    set "json={\"name\":\"!petName!\",\"photoUrls\":[\"http://example.com/photo!%%i!_1.jpg\",\"http://example.com/photo!%%i!_2.jpg\"],\"tags\":[{\"name\":\"!tagName1!\"},{\"name\":\"!tagName2!\"}],\"petStatus\":\"AVAILABLE\",\"categories\":[{\"name\":\"!categoryName1!\"},{\"name\":\"!categoryName2!\"}]}"

    if %%i neq %numPets% (
        set payload=!payload!!json!,
    ) else (
        set payload=!payload!!json!
    )
)
set payload=!payload!]

:: Close JSON array
set payload=!payload!]

:retry
:: Call the API
curl -X POST "%API_URL%:8081/pet/bulk" -H "Content-Type: application/json" -d "!payload!" --fail --silent --show-error
if %errorlevel% neq 0 (
    echo "API call failed, retrying..."
    timeout /T 10
    goto retry
)

echo "Pets initialized successfully."
