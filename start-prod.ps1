# KP Run Coach - Production Startup Script
Write-Host "Starting KP Run Coach in production mode (PostgreSQL)..." -ForegroundColor Green
Write-Host ""
Write-Host "Make sure to set the following environment variables:" -ForegroundColor Yellow
Write-Host "  SPRING_PROFILES_ACTIVE=prod"
Write-Host "  DATABASE_URL=jdbc:postgresql://your-host:5432/kpruncoach"
Write-Host "  DATABASE_USERNAME=your-username"
Write-Host "  DATABASE_PASSWORD=your-password"
Write-Host ""
Write-Host "Current environment:" -ForegroundColor Cyan
Write-Host "  SPRING_PROFILES_ACTIVE: $env:SPRING_PROFILES_ACTIVE"
Write-Host "  DATABASE_URL: $env:DATABASE_URL"
Write-Host "  DATABASE_USERNAME: $env:DATABASE_USERNAME"
Write-Host ""

if ($env:SPRING_PROFILES_ACTIVE -eq "prod") {
    Write-Host "Starting application..." -ForegroundColor Green
    & .\gradlew.bat bootRun
} else {
    Write-Host "ERROR: SPRING_PROFILES_ACTIVE must be set to 'prod'" -ForegroundColor Red
    Write-Host "Please set the required environment variables first." -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
}
