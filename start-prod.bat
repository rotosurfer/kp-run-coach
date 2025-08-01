@echo off
REM KP Run Coach - Production Startup Script
echo Starting KP Run Coach in production mode (PostgreSQL)...
echo.
echo Make sure to set the following environment variables:
echo   SPRING_PROFILES_ACTIVE=prod
echo   DATABASE_URL=jdbc:postgresql://your-host:5432/kpruncoach
echo   DATABASE_USERNAME=your-username  
echo   DATABASE_PASSWORD=your-password
echo.
echo Current environment:
echo   SPRING_PROFILES_ACTIVE=%SPRING_PROFILES_ACTIVE%
echo   DATABASE_URL=%DATABASE_URL%
echo   DATABASE_USERNAME=%DATABASE_USERNAME%
echo.
if "%SPRING_PROFILES_ACTIVE%"=="prod" (
    echo Starting application...
    gradlew.bat bootRun
) else (
    echo ERROR: SPRING_PROFILES_ACTIVE must be set to 'prod'
    echo Please set the required environment variables first.
    pause
)
