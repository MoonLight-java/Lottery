@echo off
cd /d "%~dp0"

echo ========================================
echo   Lottery System Launcher
echo ========================================
echo.

echo === Checking environment ===
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] mvn not found. Please install Maven and add to PATH.
    pause
    exit /b 1
)

if not exist "frontend\node_modules" (
    echo First run detected, installing frontend dependencies...
    cd frontend
    call npm install
    cd ..
)

echo.
echo [1/2] Starting backend API (port 8080)...
start "Lottery-API" cmd /k "cd backend && mvn spring-boot:run"

echo [2/2] Starting frontend dev server (port 3000)...
start "Lottery-UI" cmd /k "cd frontend && npx vite --host"

echo.
echo Backend:  http://localhost:8080/api/lottery
echo Frontend: http://localhost:3000
echo.
echo Close the two popup windows to stop services.
echo ========================================
pause
