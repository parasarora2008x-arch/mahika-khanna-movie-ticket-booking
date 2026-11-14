@echo off
title Run System Integration Tests
echo =====================================================================
echo    RUNNING 10-POINT AUTOMATED SYSTEM INTEGRATION TESTS
echo =====================================================================
echo.

if not defined JAVA_HOME (
    if exist "C:\Users\Lenovo\.jdks\openjdk-26.0.2\bin\java.exe" (
        set "JAVA_HOME=C:\Users\Lenovo\.jdks\openjdk-26.0.2"
    )
)

if defined JAVA_HOME (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

java -cp "target\MovieTicketBooking-1.0.0.jar;target\lib\*" SystemIntegrationTest
echo.
pause
