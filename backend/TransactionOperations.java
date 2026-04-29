public class TransactionOperations extends DBOperations<Transaction> {
    private Transaction transaction;
    private TransactionGroup transGroup;
	private DatabaseConnection dbConnection;

    @Override
    protected boolean create(Transaction transaction) {
        if (transaction == null || transaction.getTransactionId() <= 0)
            throw new IllegalArgumentException("Invalid transaction or transaction ID.");
        String query = "INSERT INTO Transaction VALUES (%d, %f, '%s', %d, '%tF', '%s', %d, %d, '%s')";
        String formatted = String.format(query, transaction.getTransactionId(), transaction.getAmount(), transaction.getType().toString(), transaction.getCategoryId(), transaction.getDate(), transaction.getReceiptPath(), transaction.getTransactionGroupId(), transaction.getProfileId(), transaction.getNote());
        
		try {
			dbConnection.getConnection().createStatement().executeUpdate(formatted);
			return true;
		} catch (Exception e) {
			return false;
		}
    }

    @Override
    protected boolean delete(Transaction transaction) {
        if (transaction == null || transaction.getTransactionId() <= 0)
            throw new IllegalArgumentException("Invalid transaction or transaction ID.");
        String query = "DELETE FROM Transaction WHERE id = %d";
        String formatted = String.format(query, transaction.getTransactionId());
        
		try {
			dbConnection.getConnection().createStatement().executeUpdate(formatted);
			return true;
		} catch (Exception e) {
			return false;
		}
    }

    public boolean updateTransactionDB(Transaction trans) {
        if (trans == null || trans.getTransactionId() <= 0)
            throw new IllegalArgumentException("Invalid transaction or transaction ID.");
        String query = "UPDATE Transaction SET amount=%f, type='%s', categoryId=%d, date=%tF, receiptPath='%s', transactionGroupId=%d, profileId=%d, note='%s' WHERE id = %d";
        String formatted = String.format(query, trans.getAmount(), trans.getType().toString(), trans.getCategoryId(), trans.getDate(), trans.getReceiptPath(), trans.getTransactionGroupId(), trans.getProfileId(), trans.getNote(), trans.getTransactionId());
        
		try {
			dbConnection.getConnection().createStatement().executeUpdate(formatted);
			return true;
		} catch (Exception e) {
			return false;
		}
    }

    public boolean createTransactionGroupDB(TransactionGroup transG) {
        if (transG == null || transG.getGroupId() <= 0)
            throw new IllegalArgumentException("Invalid transaction group or group ID.");
        String query = "INSERT INTO TransactionGroup VALUES (%d, '%s', '%s', '%s')";
        String formatted = String.format(query, transG.getGroupId(), transG.getName(), transG.getDescription(), transG.getReceiptFilePath());
        
		try {
			dbConnection.getConnection().createStatement().executeUpdate(formatted);
			return true;
		} catch (Exception e) {
			return false;
		}
    }

    public boolean deleteTransactionGroupDB(int transGrpID) {
        if (transGrpID <= 0)
            throw new IllegalArgumentException("Invalid transaction group ID.");
        String query = "DELETE FROM TransactionGroup WHERE id = %d";
        String formatted = String.format(query, transGrpID);
        
		try {
			dbConnection.getConnection().createStatement().executeUpdate(formatted);
			return true;
		} catch (Exception e) {
			return false;
		}
    }

    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction t) { transaction = t; }
    public TransactionGroup getTransactionGroup() { return transGroup; }
    public void setTransactionGroup(TransactionGroup g) { transGroup = g; }
	public DatabaseConnection getDatabaseConnection() { return dbConnection; }
	public void setDatabaseConnection(DatabaseConnection db) { dbConnection = db; }
}