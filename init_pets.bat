@echo off
setlocal enabledelayedexpansion

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
    set /a "nameIdx=%%i %% 10"
    set /a "tagIdx1=%%i %% 5"
    set /a "tagIdx2=(%%i+1) %% 5"
    set /a "categoryIdx1=%%i %% 5"
    set /a "categoryIdx2=(%%i+1) %% 5"
    
    :: Add a unique suffix to each name
    set "uniqueName=!names[!nameIdx!]!%%i"
    
    set "json={\"name\":\"!uniqueName!\",\"photoUrls\":[\"http://example.com/photo!%%i!_1.jpg\",\"http://example.com/photo!%%i!_2.jpg\"],\"tags\":[{\"tag\":{\"name\":\"!tags[!tagIdx1!]!\"}},{\"tag\":{\"name\":\"!tags[!tagIdx2!]!\"}}],\"petStatus\":\"AVAILABLE\",\"categories\":[{\"category\":{\"name\":\"!categories[!categoryIdx1!]!\"}},{\"category\":{\"name\":\"!categories[!categoryIdx2!]!\"}}]}"
    
    if %%i neq %numPets% (
        set payload=!payload!!json!,
    ) else (
        set payload=!payload!!json!
    )
)
set payload=!payload!]

:: Close JSON array
set payload=!payload!]

:: Call the API
curl -X POST "http://34.79.119.8:8080/pet/bulk" -H "Content-Type: application/json" -d "!payload!"

endlocal
cmd /k
