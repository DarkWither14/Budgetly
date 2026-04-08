# ─────────────────────────────────────────────
#  Budgetly – build & run (Windows PowerShell)
# ─────────────────────────────────────────────

$LIB_DIR  = "lib"
$JDBC_JAR = "$LIB_DIR\mysql-connector-j.jar"
$OUT_DIR  = "out"
$JDBC_URL = "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar"

# Download the JDBC driver if it's not already present
if (-not (Test-Path $JDBC_JAR)) {
    Write-Host "Downloading MySQL JDBC driver..."
    New-Item -ItemType Directory -Force -Path $LIB_DIR | Out-Null
    Invoke-WebRequest -Uri $JDBC_URL -OutFile $JDBC_JAR
}

# Compile all Java source files
Write-Host "Compiling..."
New-Item -ItemType Directory -Force -Path $OUT_DIR | Out-Null
javac -cp ".;$JDBC_JAR" backend\*.java -d $OUT_DIR

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed." -ForegroundColor Red
    exit 1
}

Write-Host "Starting Budgetly..."
Write-Host ""

# Set DB credentials here or as environment variables before running:
#   $env:BUDGETLY_DB_URL  = "jdbc:mysql://localhost:3306/budgetly"
#   $env:BUDGETLY_DB_USER = "root"
#   $env:BUDGETLY_DB_PASS = "yourpassword"
java -cp "$OUT_DIR;$JDBC_JAR" Main
