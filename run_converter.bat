if %ERRORLEVEL% EQU 0 (
    echo         Conversion Completed Successfully!
    echo ================================================================
    echo.
    echo Your PDF file has been created at:
    echo C:\Users\pmspp\AppData\Roaming\JetBrains\IntelliJIdea2025.2\extensions\PRODATA\PBP_CV.pdf
) else (
    echo         Conversion Failed - Please check the error above
    echo ================================================================
)
echo.
pause
@echo off
REM ================================================================
REM Word to PDF Converter - Automated Batch Script
REM ================================================================
REM This script builds and runs the Word to PDF converter program
REM Double-click this file to convert your Word document to PDF

setlocal enabledelayedexpansion

echo.
echo ================================================================
echo         Word Document to PDF Converter - Batch Script
echo ================================================================
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo.
    echo Please install Maven from: https://maven.apache.org/download.cgi
    echo.
    pause
    exit /b 1
)

REM Check if Java is installed
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java is not installed or not in PATH
    echo.
    echo Please install Java 21 or higher
    echo.
    pause
    exit /b 1
)

REM Change to project directory
cd /d "%~dp0"

echo Building project...
echo.
call mvn clean compile

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Build failed!
    echo.
    pause
    exit /b 1
)

echo.
echo ================================================================
echo Running Word to PDF Converter...
echo ================================================================
echo.

call mvn exec:java -Dexec.mainClass="com.java.ConvertDocToPdf"

echo.
echo.
echo ================================================================

