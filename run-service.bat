@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

echo ===================================
echo API Gateway Test - 서비스 실행기
echo ===================================
echo.
echo 실행할 서비스를 선택하세요:
echo 1. Eureka Server
echo 2. Audio Server
echo 3. API Gateway
echo 4. 모든 서비스 실행 (별도 창에서)
echo 5. 종료
echo.

set /p choice=선택하세요 (1-5): 

if "%choice%"=="1" (
    echo Eureka Server 시작 중...
    start cmd /k "title Eureka Server && gradlew :eureka_server:bootRun"
) else if "%choice%"=="2" (
    echo Audio Server 시작 중...
    start cmd /k "title Audio Server && gradlew :audio_server:bootRun"
) else if "%choice%"=="3" (
    echo API Gateway 시작 중...
    start cmd /k "title API Gateway && gradlew :api_gateway:bootRun"
) else if "%choice%"=="4" (
    echo 권장 순서대로 모든 서비스 시작 중...
    echo.
    echo Eureka Server 시작 중...
    start cmd /k "title Eureka Server && gradlew :eureka_server:bootRun"
    
    echo Eureka Server 시작 대기 중...
    timeout /t 20 /nobreak
    
    echo Audio Server 시작 중...
    start cmd /k "title Audio Server && gradlew :audio_server:bootRun"
    
    echo Audio Server 시작 대기 중...
    timeout /t 15 /nobreak
    
    echo API Gateway 시작 중...
    start cmd /k "title API Gateway && gradlew :api_gateway:bootRun"
    
    echo 모든 서비스가 시작되었습니다!
) else if "%choice%"=="5" (
    echo 종료 중...
    exit /b 0
) else (
    echo 잘못된 선택입니다. 다시 시도하세요.
    exit /b 1
)

echo 서비스가 성공적으로 시작되었습니다!
endlocal