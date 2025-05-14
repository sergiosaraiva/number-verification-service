@echo off
echo Testing Number Verification Microservice API...
echo.

echo 1. Testing POST /verify endpoint:
curl -X POST http://localhost:8080/api/v1/verify -H "Content-Type: application/json" -H "Authorization: Bearer test-token" -d "{\"phoneNumber\": \"+1234567890\", \"correlationId\": \"test-123\"}"
echo.
echo.

echo 2. Testing GET /device-phone-number endpoint:
curl -X GET http://localhost:8080/api/v1/device-phone-number -H "Authorization: Bearer test-token"

echo.
echo.

echo Testing completed.
pause
