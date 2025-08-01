@echo off
REM KP Run Coach - Development Startup Script
echo Starting KP Run Coach in development mode (SQLite)...
echo.
echo Database: SQLite (kp-run-coach-dev.db in your home directory)
echo Profile: dev
echo Port: 9090
echo.
echo Press Ctrl+C to stop the application
echo.
gradlew.bat bootRun
