# ============================================================
# Budgetly - Run ChartPrinter JUnit Tests
# Place this file inside: Budgetly/Unit_Testing/
# ============================================================

$ErrorActionPreference = "Stop"

# This assumes:
# Budgetly/
# ├── backend/              <-- your main .java files
# ├── Unit_Testing/         <-- this script + ChartPrinterTest.java
# ├── lib/                  <-- JUnit jar will go here
# └── out/                  <-- compiled .class files go here

$ProjectRoot = Split-Path -Parent $PSScriptRoot
$BackendDir  = Join-Path $ProjectRoot "backend"
$TestDir     = $PSScriptRoot
$LibDir      = Join-Path $ProjectRoot "lib"
$OutDir      = Join-Path $ProjectRoot "out"

$TestFile    = Join-Path $TestDir "ChartPrinterTest.java"
$TestClass   = "ChartPrinterTest"

$JUnitJarName = "junit-platform-console-standalone-1.10.2.jar"
$JUnitJar     = Join-Path $LibDir $JUnitJarName
$JUnitUrl     = "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/$JUnitJarName"

# Optional MySQL connector, only needed if your tests instantiate Controller/database code
$JdbcJar = Join-Path $LibDir "mysql-connector-j.jar"

Write-Host "=== Budgetly ChartPrinter Test Runner ==="

# Create folders
if (!(Test-Path $LibDir)) {
    New-Item -ItemType Directory -Path $LibDir | Out-Null
}

if (!(Test-Path $OutDir)) {
    New-Item -ItemType Directory -Path $OutDir | Out-Null
}

# Download JUnit if missing
if (!(Test-Path $JUnitJar)) {
    Write-Host "Downloading JUnit 5 standalone jar..."
    Invoke-WebRequest -Uri $JUnitUrl -OutFile $JUnitJar
} else {
    Write-Host "JUnit jar found."
}

# Validate folders/files
if (!(Test-Path $BackendDir)) {
    Write-Host "ERROR: Could not find backend folder:"
    Write-Host $BackendDir
    Write-Host ""
    Write-Host "If your .java files are directly inside Budgetly instead of Budgetly/backend,"
    Write-Host "change this line:"
    Write-Host '$BackendDir  = Join-Path $ProjectRoot "backend"'
    Write-Host "to:"
    Write-Host '$BackendDir  = $ProjectRoot'
    exit 1
}

if (!(Test-Path $TestFile)) {
    Write-Host "ERROR: Could not find test file:"
    Write-Host $TestFile
    Write-Host ""
    Write-Host "Make sure your test file is named ChartPrinterTest.java"
    exit 1
}

if (!(Test-Path $JdbcJar)) {
    Write-Host "Warning: MySQL connector not found at:"
    Write-Host $JdbcJar
    Write-Host "This is okay if ChartPrinterTest does not create a Controller."
}

# Clean old compiled output
Write-Host "Cleaning old compiled files..."
Remove-Item -Recurse -Force $OutDir -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path $OutDir | Out-Null

# Compile
Write-Host "Compiling project and test..."

$JavaFiles = Get-ChildItem -Path $BackendDir -Filter "*.java" | ForEach-Object {
    $_.FullName
}

$Classpath = "$JUnitJar;$JdbcJar;$BackendDir;$TestDir"

javac -cp $Classpath -d $OutDir $JavaFiles $TestFile

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed."
    exit 1
}

# Run tests
Write-Host "Running tests..."

$RunClasspath = "$OutDir;$JUnitJar;$JdbcJar"

java -cp $RunClasspath org.junit.platform.console.ConsoleLauncher --select-class $TestClass

if ($LASTEXITCODE -ne 0) {
    Write-Host "Some tests failed."
    exit 1
}

Write-Host "All tests passed."