@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo ========================================
echo  启动抽奖系统
echo ========================================
echo.

echo [1/2] 启动后端 API (Spring Boot :8080)...
start "Lottery-API" cmd /c "cd backend && mvn spring-boot:run -q"

echo [2/2] 启动前端开发服务器 (Vite :3000)...
start "Lottery-UI" cmd /c "cd frontend && npx vite --host"

echo.
echo 后端: http://localhost:8080/api/lottery
echo 前端: http://localhost:3000
echo.
echo 两个窗口已打开，关闭窗口即停止服务。
echo ========================================
pause
