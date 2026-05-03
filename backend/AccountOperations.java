public class AccountOperations extends DBOperations<Account> {
    private Account account;
    private Profile profile;
    private DatabaseConnection dbConnection;

    @Override
    protected boolean create(Account account) {
        if (account == null || account.getAccountID() <= 0)
            throw new IllegalArgumentException("Invalid account or account ID.");
        String query = "INSERT INTO Account VALUES (%d, '%s', %d)";
        String formatted = String.format(query, account.getAccountID(), account.getEmail(), account.getPasswordHash());
        try {
            dbConnection.getConnection().createStatement().executeUpdate(formatted);
            System.out.println("Account saved to database: " + account.getEmail());
            return true;
        } catch (Exception e) {
            System.err.println("Error saving account to database: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected boolean delete(Account account) {
        if (account == null || account.getAccountID() <= 0)
            throw new IllegalArgumentException("Invalid account or account ID.");
        String query = "DELETE FROM Account WHERE accountID = %d";
        String formatted = String.format(query, account.getAccountID());
        try {
            dbConnection.getConnection().createStatement().executeUpdate(formatted);
            System.out.println("Account deleted from database: " + account.getEmail());
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting account from database: " + e.getMessage());
            return false;
        }
    }

    public boolean addProfileDB(Profile profile, int accountID) {
        String statement = "INSERT INTO Profile VALUES (%d, %d, '%s', '%s', %f)";
        statement = String.format(statement,
                profile.getID(),
                accountID,
                profile.getDisplayName(),
                profile.getDescription() != null ? profile.getDescription() : "",
                profile.getBankRoll());
        try {
            dbConnection.getConnection().createStatement().executeUpdate(statement);
            System.out.println("Profile saved to database: " + profile.getDisplayName());
            return true;
        } catch (Exception e) {
            System.err.println("Error saving profile to database: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProfileDB(Profile profile) {
        String statement = "DELETE FROM Profile WHERE id = %d";
        statement = String.format(statement, profile.getID());
        try {
            dbConnection.getConnection().createStatement().executeUpdate(statement);
            System.out.println("Profile deleted from database: " + profile.getDisplayName());
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting profile from database: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProfileDB(Profile profile) {
        if (profile == null || profile.getID() <= 0)
            throw new IllegalArgumentException("Invalid profile or profile ID.");
        String statement = "UPDATE Profile SET displayName='%s', description='%s', bankRoll=%f WHERE id = %d";
        statement = String.format(statement,
                profile.getDisplayName(),
                profile.getDescription() != null ? profile.getDescription() : "",
                profile.getBankRoll(),
                profile.getID());
        try {
            dbConnection.getConnection().createStatement().executeUpdate(statement);
            System.out.println("Profile updated in database: " + profile.getDisplayName());
            return true;
        } catch (Exception e) {
            System.err.println("Error updating profile in database: " + e.getMessage());
            return false;
        }
    }

    public Account getAccount() { return account; }
    public void setAccount(Account a) { account = a; }
    public Profile getProfile() { return profile; }
    public void setProfile(Profile p) { profile = p; }
    public DatabaseConnection getDatabaseConnection() { return dbConnection; }
    public void setDatabaseConnection(DatabaseConnection db) { dbConnection = db; }
}