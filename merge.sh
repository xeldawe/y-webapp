#!/bin/sh

# Check if global.env and API_KEY.env have content
if [ ! -s global.env ]; then
  echo "global.env is empty or does not exist."
  exit 1
fi

if [ ! -s API_KEY.env ]; then
  echo "API_KEY.env is empty or does not exist."
  exit 1
fi

# Display the content of global.env
echo "Content of global.env:"
cat global.env

# Display the content of API_KEY.env
echo "Content of API_KEY.env:"
cat API_KEY.env

# Merge the environment files into a single file
cat global.env API_KEY.env > merged.env

# Check if merged.env was created successfully and has content
if [ ! -s merged.env ]; then
  echo "Failed to merge environment files or merged.env is empty."
  exit 1
fi

# Display the content of merged.env
echo "Content of merged.env:"
cat merged.env

