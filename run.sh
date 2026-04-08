#!/bin/bash

# ─────────────────────────────────────────────
#  Budgetly – build & run (terminal mode)
# ─────────────────────────────────────────────

LIB_DIR="lib"
JDBC_JAR="$LIB_DIR/mysql-connector-j.jar"
OUT_DIR="out"

# MySQL Connector/J 8.3.0 from Maven Central
JDBC_URL="https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar"

# Download the JDBC driver if it's not already present
if [ ! -f "$JDBC_JAR" ]; then
    echo "Downloading MySQL JDBC driver..."
    mkdir -p "$LIB_DIR"
    curl -L "$JDBC_URL" -o "$JDBC_JAR"
fi

# Compile all Java source files
echo "Compiling..."
mkdir -p "$OUT_DIR"
javac -cp ".:$JDBC_JAR" backend/*.java -d "$OUT_DIR"

if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi

echo "Starting Budgetly..."
echo ""

# Run — set DB credentials via environment variables before running, e.g.:
#   export BUDGETLY_DB_URL=jdbc:mysql://localhost:3306/budgetly
#   export BUDGETLY_DB_USER=root
#   export BUDGETLY_DB_PASS=yourpassword
java -cp "$OUT_DIR:$JDBC_JAR" Main
