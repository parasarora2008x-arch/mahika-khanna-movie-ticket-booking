@echo off
title Movie Ticket Booking Management System
echo =====================================================================
echo    MOVIE TICKET BOOKING MANAGEMENT SYSTEM (JAVA + MYSQL)
echo =====================================================================
cd /d "%~dp0"

:: Detect JAVA
if not defined JAVA_HOME (
    if exist "C:\Users\Lenovo\.jdks\openjdk-26.0.2\bin\java.exe" (
        set "JAVA_HOME=C:\Users\Lenovo\.jdks\openjdk-26.0.2"
    )
)

if defined JAVA_HOME (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

echo Starting Java Swing Application...
java -cp "target\MovieTicketBooking-1.0.0.jar;target\lib\*" Main

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Application exited with error code %ERRORLEVEL%.
    pause
)
