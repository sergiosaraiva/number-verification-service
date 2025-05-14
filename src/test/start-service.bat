@echo off
echo Checking if Number Verification Service is running...

REM Check if the application is running by trying to connect to the health endpoint
curl -s -o nul -w "%%{http_code}" http://localhost:8080/api/v1/actuator/health > temp.txt
set /p status=<temp.txt
del temp.txt

if "%status%"=="200" (
    echo Service is already running.
) else (
    echo Service is not running. Starting it now...
    start "Number Verification Service" cmd /c "gradle bootRun"
    
    echo Waiting for service to start...
    timeout /t 10
    
    REM Check again if the service started successfully
    curl -s -o nul -w "%%{http_code}" http://localhost:8080/api/v1/actuator/health > temp.txt
    set /p status=<temp.txt
    del temp.txt
    
    if "%status%"=="200" (
        echo Service started successfully.
    ) else (
        echo Failed to start the service. Please check the logs.
    )
)

echo You can now run test-api.bat to test the endpoints.
pause
