@echo off
setlocal enabledelayedexpansion

echo Checking if Number Verification Service is running...

REM Check if the application is running by trying to connect to the health endpoint
curl -s -o nul -w "%%{http_code}" http://localhost:8080/api/v1/actuator/health > temp.txt
set /p status=<temp.txt
del temp.txt

if "%status%"=="200" (
    echo Service is already running.
) else (
    echo Service is not running. Starting it in the background...
    
    REM Start the service in background using javaw
    start /B "" cmd /c "gradle bootRun > service.log 2>&1"
    
    echo Waiting for service to start...
    
    REM Loop to check if service is running
    for /l %%i in (1, 1, 12) do (
        timeout /t 5 /nobreak > nul
        curl -s -o nul -w "%%{http_code}" http://localhost:8080/api/v1/actuator/health > temp.txt
        set /p status=<temp.txt
        del temp.txt
        
        if "!status!"=="200" (
            echo Service started successfully.
            goto :done
        )
    )
    
    echo Timeout waiting for service to start. Check service.log for details.
)

:done
echo You can now run test-api.bat to test the endpoints.
