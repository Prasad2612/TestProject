# ================================================================
# Word to PDF Converter - PowerShell Script
# ================================================================
# Run this script with: powershell -ExecutionPolicy Bypass -File run_converter.ps1
# Or right-click and select "Run with PowerShell"

Write-Host "`n"
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host "         Word Document to PDF Converter" -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host ""

# Check if Maven is installed
$mavenCheck = & cmd /c "where mvn" 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Maven is not installed or not in PATH" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please install Maven from: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

# Check if Java is installed
$javaCheck = & cmd /c "where java" 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Java is not installed or not in PATH" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please install Java 21 or higher" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

# Get script directory
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptPath

Write-Host "Building project..." -ForegroundColor Green
Write-Host ""

# Build
& mvn clean compile
if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERROR: Build failed!" -ForegroundColor Red
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "================================================================" -ForegroundColor Green
Write-Host "Running Word to PDF Converter..." -ForegroundColor Green
Write-Host "================================================================" -ForegroundColor Green
Write-Host ""

# Run the converter
& mvn exec:java -Dexec.mainClass="com.java.ConvertDocToPdf"

Write-Host ""
Write-Host ""
Write-Host "================================================================" -ForegroundColor Cyan
if ($LASTEXITCODE -eq 0) {
    Write-Host "         Conversion Completed Successfully!" -ForegroundColor Green
    Write-Host "================================================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Your PDF file has been created at:" -ForegroundColor Green
    Write-Host "C:\Users\pmspp\AppData\Roaming\JetBrains\IntelliJIdea2025.2\extensions\PRODATA\PBP_CV.pdf" -ForegroundColor Yellow
} else {
    Write-Host "         Conversion Failed - Please check the error above" -ForegroundColor Red
    Write-Host "================================================================" -ForegroundColor Cyan
}
Write-Host ""
Read-Host "Press Enter to exit"

