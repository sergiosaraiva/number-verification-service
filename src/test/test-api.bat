@echo off
echo Testing Number Verification Microservice API...
echo.

echo 1. Testing POST /verify endpoint:
curl -X POST http://localhost:8080/api/v1/verify -H "Content-Type: application/json" -H "Authorization: Bearer mock_sandbox_access_token" -d "{\"phoneNumber\": \"+12345678901\"}"
echo.
echo.

echo 2. Testing POST /verify endpoint:
curl -X POST http://localhost:8080/api/v1/verify -H "Content-Type: application/json" -H "Authorization: Bearer mock_sandbox_access_token" -d "{\"hashedPhoneNumber\": \"a hash value\"}"
echo.
echo.

echo 3. Testing GET /device-phone-number endpoint:
curl -X GET http://localhost:8080/api/v1/device-phone-number -H "Authorization: Bearer mock_sandbox_access_token"

echo.
echo.

echo Testing completed.
pause
