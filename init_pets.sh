#!/bin/bash

# Load the URL and port from the global.env file
source global.env

# Number of pets to insert
numPets=10

# Arrays for names, tags, and categories
names=("Buddy" "Max" "Bella" "Charlie" "Lucy" "Daisy" "Molly" "Bailey" "Lola" "Stella")
tags=("tag1" "tag2" "tag3" "tag4" "tag5")
categories=("category1" "category2" "category3" "category4" "category5")

# Create JSON payload
payload="["

for i in $(seq 1 $numPets); do
    # Generate random indexes for names, tags, and categories
    nameIdx=$((RANDOM % 10))
    tagIdx1=$((RANDOM % 5))
    tagIdx2=$((RANDOM % 5))
    categoryIdx1=$((RANDOM % 5))
    categoryIdx2=$((RANDOM % 5))

    # Use the name from the names array
    petName="${names[$nameIdx]}"
    tagName1="${tags[$tagIdx1]}"
    tagName2="${tags[$tagIdx2]}"
    categoryName1="${categories[$categoryIdx1]}"
    categoryName2="${categories[$categoryIdx2]}"

    # Construct JSON for this pet
    json="{\"name\":\"$petName\",\"photoUrls\":[\"http://example.com/photo${i}_1.jpg\",\"http://example.com/photo${i}_2.jpg\"],\"tags\":[{\"name\":\"$tagName1\"},{\"name\":\"$tagName2\"}],\"petStatus\":\"AVAILABLE\",\"categories\":[{\"name\":\"$categoryName1\"},{\"name\":\"$categoryName2\"}]}"

    if [ $i -ne $numPets ]; then
        payload="$payload$json,"
    else
        payload="$payload$json"
    fi
done

payload="$payload]"

# Call the API
curl -X POST "${API_URL}:8081/pet/bulk" -H "Content-Type: application/json" -d "$payload"

