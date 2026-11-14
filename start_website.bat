@echo off
title Movie Ticket Booking Web Portal
echo =====================================================================
echo    MOVIE TICKET BOOKING MANAGEMENT SYSTEM - WEB PORTAL
echo    Website URL: http://localhost:8080
echo    Database:    movie_ticket_db (MySQL Standalone)
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

:: Automatically open browser
start "" http://localhost:8080

:: Run the pure Core Java Web Server
java -cp "target\MovieTicketBooking-1.0.0.jar;target\lib\*" WebServer

pause
