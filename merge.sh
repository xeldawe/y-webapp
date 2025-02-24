#!/bin/sh

# Check if global.env and api-key.env have content
if [ ! -s global.env ]; then
  echo "global.env is empty or does not exist."
  exit 1
fi

if [ ! -s api-key.env ]; then
  echo "api-key.env is empty or does not exist."
  exit 1
fi

# Display the content of global.env
echo "Content of global.env:"
cat global.env

# Display the content of api-key.env
echo "Content of api-key.env:"
cat api-key.env

# Merge the environment files into a single file
cat global.env api-key.env > merged.env

# Check if merged.env was created successfully and has content
if [ ! -s merged.env ]; then
  echo "Failed to merge environment files or merged.env is empty."
  exit 1
fi

# Display the content of merged.env
echo "Content of merged.env:"
cat merged.env

