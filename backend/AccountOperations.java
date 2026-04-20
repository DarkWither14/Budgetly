public class AccountOperations extends DBOperations {
	private Account account;
	private Profile profile;

	// Required by DBOperations, but not used for AccountOperations
	@Override
	protected boolean create(Category category) {
		throw new UnsupportedOperationException("Not supported for AccountOperations");
	}

	@Override
	protected boolean delete(Category category) {
		throw new UnsupportedOperationException("Not supported for AccountOperations");
	}

	// Account-specific create
	protected boolean create(Account account) {
		String query = "INSERT INTO Account VALUES (%d, %s, %d)";
		String formatted = String.format(query, account.getAccountID(), account.getEmail(), account.getPasswordHash());
		// TODO: Execute the query
		return true;
	}

	// Account-specific delete
	protected boolean delete(Account account) {
		String query = "DELETE FROM Account WHERE accountID = %d";
		String formatted = String.format(query, account.getAccountID());
		// TODO: Execute the query
		return true;
	}

	public boolean updateProfileDB(Profile profile) {
		//replace fields with name of fields in DB
		String query = "UPDATE Profile SET displayName=%s, description=%s, bankRoll=%f WHERE id = %d";
		String.format(query, profile.getDisplayName(), profile.getDescription(), profile.getBankRoll(), profile.getID());

		return true;
	}

	public Account getAccount() { return account; }
	public void setAccount(Account a) { account = a; }
	public Profile getProfile() { return profile; }
	public void setProfile(Profile p) { profile = p; }
}
