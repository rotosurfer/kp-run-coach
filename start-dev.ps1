# KP Run Coach - Development Startup Script
Write-Host "Starting KP Run Coach in development mode (SQLite)..." -ForegroundColor Green
Write-Host ""
Write-Host "Database: SQLite (kp-run-coach-dev.db in your home directory)" -ForegroundColor Cyan
Write-Host "Profile: dev" -ForegroundColor Cyan
Write-Host "Port: 9090" -ForegroundColor Cyan
Write-Host ""
Write-Host "Press Ctrl+C to stop the application" -ForegroundColor Yellow
Write-Host ""
& .\gradlew.bat bootRun
