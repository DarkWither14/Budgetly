# ─────────────────────────────────────────────
#  Budgetly – unit tests (Windows PowerShell)
# ─────────────────────────────────────────────

$LIB_DIR  = "lib"
$JDBC_JAR = "$LIB_DIR\mysql-connector-j.jar"
$JUNIT_JAR = "$LIB_DIR\junit-platform-console-standalone.jar"
$OUT_DIR  = "out"
$JUNIT_URL = "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.0/junit-platform-console-standalone-1.10.0.jar"

if (-not (Test-Path $JUNIT_JAR)) {
    Write-Host "Downloading JUnit..."
    New-Item -ItemType Directory -Force -Path $LIB_DIR | Out-Null
    Invoke-WebRequest -Uri $JUNIT_URL -OutFile $JUNIT_JAR
}

Write-Host "Compiling..."
New-Item -ItemType Directory -Force -Path $OUT_DIR | Out-Null
javac -cp ".;$JDBC_JAR;$JUNIT_JAR" backend\*.java Unit_Testing\AddProfileTest.java -d $OUT_DIR

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed." -ForegroundColor Red
    exit 1
}

Write-Host "Running unit tests..."
java -cp "$OUT_DIR;$JDBC_JAR;$JUNIT_JAR" org.junit.platform.console.ConsoleLauncher --select-class=AddProfileTest