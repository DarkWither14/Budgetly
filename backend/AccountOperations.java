public class AccountOperations {
	private Account account;

	public boolean addProfileDB(Profile profile, DatabaseConnection dbConnection) {
		// Reference to Account omitted from insert
		String statement = "INSERT INTO Profile VALUES (%d, '%s', '%s', %f)";
		String.format(statement, profile.getID(), profile.getDisplayName(), profile.getDescription(), profile.getBankRoll());

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean deleteProfileDB(Profile profile, DatabaseConnection dbConnection) {
		String statement = "DELETE FROM Profile WHERE id = %d";
		String.format(statement, profile.getID());

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean updateProfileDB(Profile profile, DatabaseConnection dbConnection) {
		//								   replace fields with name of fields in DB
		String statement = "UPDATE Profile SET displayName='%s', description='%s', bankRoll=%f WHERE id = %d";
		String.format(statement, profile.getDisplayName(), profile.getDescription(), profile.getBankRoll(), profile.getID());

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		} 
	}

	public Account getAccount() { return account; }
	public void setAccount(Account a) { account = a; }
}
