public class AccountOperations extends DBOperations<Account> {
    private Account account;
    private Profile profile;

    @Override
    protected boolean create(Account account) {
        if (account == null || account.getAccountID() <= 0)
            throw new IllegalArgumentException("Invalid account or account ID.");
        String query = "INSERT INTO Account VALUES (%d, %s, %d)";
        String formatted = String.format(query, account.getAccountID(), account.getEmail(), account.getPasswordHash());
        // TODO: Execute the query
        return true;
    }

    @Override
    protected boolean delete(Account account) {
        if (account == null || account.getAccountID() <= 0)
            throw new IllegalArgumentException("Invalid account or account ID.");
        String query = "DELETE FROM Account WHERE accountID = %d";
        String formatted = String.format(query, account.getAccountID());
        // TODO: Execute the query
        return true;
    }

    public boolean updateProfileDB(Profile profile) {
        if (profile == null || profile.getID() <= 0)
            throw new IllegalArgumentException("Invalid profile or profile ID.");
        String query = "UPDATE Profile SET displayName=%s, description=%s, bankRoll=%f WHERE id = %d";
        String formatted = String.format(query, profile.getDisplayName(), profile.getDescription(), profile.getBankRoll(), profile.getID());
        // TODO: Execute the query
        return true;
    }

    public Account getAccount() { return account; }
    public void setAccount(Account a) { account = a; }
    public Profile getProfile() { return profile; }
    public void setProfile(Profile p) { profile = p; }
}