public class DatabaseOperations {
    private DatabaseConnection dbConnection;

    public boolean initializeDatabase() {
        String statement = "CREATE DATABASE IF NOT EXISTS budgetly";
        try {
            dbConnection.getConnection().createStatement().executeUpdate(statement);
            System.out.println("Database initialized successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            return false;
        }
    }

    public boolean initializeTables() {
        String[] statements = {
            "CREATE TABLE IF NOT EXISTS Account (accountID INTEGER PRIMARY KEY, email VARCHAR(50), passwordHash INTEGER)",
            "CREATE TABLE IF NOT EXISTS Profile (id INTEGER PRIMARY KEY, accountID INTEGER, displayName VARCHAR(25), description VARCHAR(100), bankRoll FLOAT(24))",
            "CREATE TABLE IF NOT EXISTS Category (id INTEGER PRIMARY KEY, profileID INTEGER, name VARCHAR(25), type VARCHAR(7), description VARCHAR(100))",
            "CREATE TABLE IF NOT EXISTS Transaction (id INTEGER PRIMARY KEY, amount FLOAT(24), type VARCHAR(7), categoryId INTEGER, date DATE, receiptPath VARCHAR(100), transactionGroupId INTEGER, profileId INTEGER, note VARCHAR(100))",
            "CREATE TABLE IF NOT EXISTS TransactionGroup (id INTEGER PRIMARY KEY, profileID INTEGER, name VARCHAR(25), description VARCHAR(100), receiptPath VARCHAR(100))"
        };
        for (String s : statements) {
            try {
                dbConnection.getConnection().createStatement().executeUpdate(s);
                System.out.println("Created table: " + s.split("EXISTS ")[1].split(" ")[0]);
            } catch (Exception e) {
                System.err.println("Error creating table: " + e.getMessage());
                return false;
            }
        }
        System.out.println("All tables initialized successfully.");
        return true;
    }

    public DatabaseConnection getDatabaseConnection() { return dbConnection; }
    public void setDatabaseConnection(DatabaseConnection db) { dbConnection = db; }
}