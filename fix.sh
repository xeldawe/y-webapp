#!/bin/bash

# Define variables
MYSQL_CONTAINER="y-webapp-mysql-1"
MYSQL_USER="test"
MYSQL_PASSWORD="testpw"
SQL_COMMANDS_FILE="integration-developer-master/db/init.sql"

# Drop the existing music database
echo "Dropping the existing music database..."
docker exec -i "$MYSQL_CONTAINER" mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "DROP DATABASE IF EXISTS music;"
if [ $? -ne 0 ]; then
    echo "Failed to drop database. Aborting script."
    exit 1
fi

# Recreate the music database
echo "Recreating the music database..."
docker exec -i "$MYSQL_CONTAINER" mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "CREATE DATABASE music;"
if [ $? -ne 0 ]; then
    echo "Failed to create database. Aborting script."
    exit 1
fi

# Run the SQL commands from the file
echo "Running init.sql to set up the schema and seed the database..."
docker exec -i "$MYSQL_CONTAINER" mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" music < "$SQL_COMMANDS_FILE"
if [ $? -ne 0 ]; then
    echo "Failed to run init.sql. Aborting script."
    exit 1
fi

echo "Done!"

