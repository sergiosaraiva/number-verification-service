@echo off
echo Stopping Number Verification Service...

REM Find Java processes running Gradle
for /f "tokens=1" %%p in ('wmic process where "commandline like '%%gradle%%bootRun%%'" get processid ^| findstr /r "[0-9]"') do (
    echo Killing process with PID: %%p
    taskkill /F /PID %%p
)

REM Check if the application is still running
timeout /t 2 /nobreak > nul
curl -s -o nul -w "%%{http_code}" http://localhost:8080/api/v1/actuator/health > temp.txt
set /p status=<temp.txt
del temp.txt

if "%status%"=="200" (
    echo Warning: Service is still running. Some processes may not have been terminated.
) else (
    echo Service stopped successfully.
)

echo Done.
