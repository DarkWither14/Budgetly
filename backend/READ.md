# Budgetly
A console-based expense tracking application that helps you moderate spending habits and more.

## Requirements
- Java 17 or later
- MySQL 8.0 or later (for full DB functionality)

## How to Run

**Windows:**
```powershell
.\run.ps1
```

**macOS / Linux:**
```bash
bash run.sh
```

The script compiles all Java source files and launches the application in your terminal.

## Database Setup
Set the following environment variables before running to connect to MySQL:
```
BUDGETLY_DB_URL   = jdbc:mysql://localhost:3306/budgetly
BUDGETLY_DB_USER  = your_username
BUDGETLY_DB_PASS  = your_password
```

## Features
- Manage multiple profiles
- Track income and expenses with categories
- Organize transactions into groups
- View reports and ASCII charts directly in the terminal
