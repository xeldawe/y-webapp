@echo off

SET /P API_KEY="Please enter your x-api-key value: "

echo Creating test orders...
curl -X POST -H "Content-Type: application/json" -H "x-api-key: %API_KEY%" -d "{\"petId\": 1, \"quantity\": 2, \"shipDate\": \"2025-02-10T20:29:00\", \"orderStatus\": \"PLACED\", \"complete\": true}" http://localhost:8080/store/order
curl -X POST -H "Content-Type: application/json" -H "x-api-key: %API_KEY%" -d "{\"petId\": 2, \"quantity\": 4, \"shipDate\": \"2025-02-11T15:15:00\", \"orderStatus\": \"PLACED\", \"complete\": true}" http://localhost:8080/store/order
curl -X POST -H "Content-Type: application/json" -H "x-api-key: %API_KEY%" -d "{\"petId\": 3, \"quantity\": 1, \"shipDate\": \"2025-02-12T10:45:00\", \"orderStatus\": \"PLACED\", \"complete\": true}" http://localhost:8080/store/order
echo.

echo Retrieving all orders before deletion...
curl -X GET -H "x-api-key: %API_KEY%" "http://localhost:8080/store/orders"
echo.

echo Deleting order with ID 1...
curl -X DELETE -H "x-api-key: %API_KEY%" "http://localhost:8080/store/orders/1"
echo.

echo Retrieving all orders after deletion...
curl -X GET -H "x-api-key: %API_KEY%" "http://localhost:8080/store/orders"
echo.

echo Updating order with ID 2...
curl -X PATCH -H "Content-Type: application/json-patch+json" -H "x-api-key: %API_KEY%" -d "[{\"op\": \"replace\", \"path\": \"/quantity\", \"value\": 3}]" http://localhost:8080/store/orders/2
echo.

echo Retrieving updated order with ID 2...
curl -X GET -H "x-api-key: %API_KEY%" "http://localhost:8080/store/orders/2"
echo.

echo Retrieving all orders after updates...
curl -X GET -H "x-api-key: %API_KEY%" "http://localhost:8080/store/orders"
echo.

echo All tests completed.
pause
