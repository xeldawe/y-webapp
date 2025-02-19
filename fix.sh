#!/bin/bash

# Define variables
MYSQL_CONTAINER="y-webapp-mysql-1"
MYSQL_USER="test"
MYSQL_PASSWORD="testpw"
SQL_COMMANDS_FILE="integration-developer-master/db/init.sql"

# Function to execute a MySQL command
execute_mysql_command() {
    docker exec -i "$MYSQL_CONTAINER" mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "$1"
    return $?
}

# Function to run SQL file
run_sql_file() {
    docker exec -i "$MYSQL_CONTAINER" mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" music < "$SQL_COMMANDS_FILE"
    return $?
}

# Retry logic
while true; do
    # Drop the existing music database
    echo "Dropping the existing music database..."
    execute_mysql_command "DROP DATABASE IF EXISTS music;"
    if [ $? -ne 0 ]; then
        echo "Failed to drop database. Retrying..."
        sleep 10
        continue
    fi

    # Recreate the music database
    echo "Recreating the music database..."
    execute_mysql_command "CREATE DATABASE music;"
    if [ $? -ne 0 ]; then
        echo "Failed to create database. Retrying..."
        sleep 10
        continue
    fi

    # Run the SQL commands from the file
    echo "Running init.sql to set up the schema and seed the database..."
    run_sql_file
    if [ $? -ne 0 ]; then
        echo "Failed to run init.sql. Retrying..."
        sleep 10
        continue
    fi

    echo "Database setup completed successfully."
    break
done
