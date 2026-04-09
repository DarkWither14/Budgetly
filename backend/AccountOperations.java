public class AccountOperations {
	private Account account;

	public boolean addProfileDB(Profile profile) {
		// Reference to Account omitted from query
		String query = "INSERT INTO Profile VALUES (%d, %s, %s, %f)";
		String.format(query, profile.getID(), profile.getDisplayName(), profile.getDescription(), profile.getBankRoll());

		return true;
	}

	public boolean deleteProfileDB(Profile profile) {
		String query = "DELETE FROM Profile WHERE id = %d";
		String.format(query, profile.getID());

		return true;
	}

	public boolean updateProfileDB(Profile profile) {
		//								   replace fields with name of fields in DB
		String query = "UPDATE Profile SET displayName=%s, description=%s, bankRoll=%f WHERE id = %d";
		String.format(query, profile.getDisplayName(), profile.getDescription(), profile.getBankRoll(), profile.getID());

		return true;
	}

	public Account getAccount() { return account; }
	public void setAccount(Account a) { account = a; }
}
