@echo off
REM Run this when Gradle/Studio says "failed to delete some children".
REM Close Android Studio first, then double-click this file.

cd /d "%~dp0"
echo Stopping Gradle daemons...
call gradlew.bat --stop
timeout /t 2 /nobreak >nul

echo Removing build folders...
if exist "app\build" rmdir /s /q "app\build"
if exist "build" rmdir /s /q "build"

if exist "app\build" (
  echo.
  echo FAILED: app\build is still locked. Close Android Studio, pause OneDrive for this folder, then run this again.
  pause
  exit /b 1
)

echo.
echo OK. Open Android Studio and use Build - Rebuild Project, then Run.
pause
