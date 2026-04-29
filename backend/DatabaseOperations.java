public class DatabaseOperations {
	private DatabaseConnection dbConnection;

	public boolean initializeDatabase() {
		String statement = "CREATE DATABASE BUDGETLY";

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean initializeTables() {
		// I know, this doesn't look good
		String[] statements = {
			"CREATE TABLE Profile (id INTEGER PRIMARY KEY, displayName VARCHAR(25), description VARCHAR(100), bankRoll FLOAT(24))",
			"CREATE TABLE Category (id INTEGER PRIMARY KEY, name VARCHAR(25), type VARCHAR(7), description VARCHAR(100))",
			"CREATE TABLE Transaction (id INTEGER PRIMARY KEY, amount FLOAT(24), type VARCHAR(7), categoryId INTEGER, date DATE, receiptPath VARCHAR(100), transactionGroupId INTEGER, profileId INTEGER, note VARCHAR(100))",
			"CREATE TABLE TransactionGroup (id INTEGER PRIMARY KEY, name VARCHAR(25), description VARCHAR(100), receiptPath VARCHAR(100))"
		};

		for (String s : statements) {
			try {
				dbConnection.getConnection().createStatement().executeUpdate(s);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	public DatabaseConnection getDatabaseConnection() { return dbConnection; }
	public void setDatabaseConnection(DatabaseConnection db) { dbConnection = db; }
}
