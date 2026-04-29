public class AccountOperations {
	private Account account;
	private DatabaseConnection dbConnection;

	public boolean addProfileDB(Profile profile) {
		// Reference to Account omitted from insert
		String statement = "INSERT INTO Profile VALUES (%d, '%s', '%s', %f)";
		statement = String.format(statement, profile.getID(), profile.getDisplayName(), profile.getDescription(), profile.getBankRoll());

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean deleteProfileDB(Profile profile) {
		String statement = "DELETE FROM Profile WHERE id = %d";
		statement = String.format(statement, profile.getID());

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean updateProfileDB(Profile profile) {
		//								   replace fields with name of fields in DB
		String statement = "UPDATE Profile SET displayName='%s', description='%s', bankRoll=%f WHERE id = %d";
		statement = String.format(statement, profile.getDisplayName(), profile.getDescription(), profile.getBankRoll(), profile.getID());

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		} 
	}

	public Account getAccount() { return account; }
	public void setAccount(Account a) { account = a; }

	public DatabaseConnection getDatabaseConnection() { return dbConnection; }
	public void setDatabaseConnection(DatabaseConnection db) { dbConnection = db; }
}
