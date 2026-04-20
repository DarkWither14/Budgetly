public class TransactionOperations extends DBOperations {
	private Transaction transaction;
	private TransactionGroup transGroup;

	// Required by DBOperations, but not used for TransactionOperations
	@Override
	protected boolean create(Category category) {
		throw new UnsupportedOperationException("Not supported for TransactionOperations");
	}

	@Override
	protected boolean delete(Category category) {
		throw new UnsupportedOperationException("Not supported for TransactionOperations");
	}

	// Transaction-specific create
	protected boolean create(Transaction transaction) {
		String query = "INSERT INTO Transaction VALUES (%d, %f, %s, %d, %tF, %s, %d, %d, %s)";
		String formatted = String.format(query, transaction.getTransactionId(), transaction.getAmount(), transaction.getType().toString(), transaction.getCategoryId(), transaction.getDate(), transaction.getReceiptPath(), transaction.getTransactionGroupId(), transaction.getProfileId(), transaction.getNote());
		// TODO: Execute the query
		return true;
	}

	// Transaction-specific delete
	protected boolean delete(Transaction transaction) {
		String query = "DELETE FROM Transaction WHERE id = %d";
		String formatted = String.format(query, transaction.getTransactionId());
		// TODO: Execute the query
		return true;
	}

	public boolean updateTransactionDB(Transaction trans) {
		// replace fields with name of fields in DB
		String query = "UPDATE Transaction SET amount=%f, type=%s, categoryId=%d, date=%tF, receiptPath=%s, transactionGroupId=%d, profileId=%d, note=%s WHERE id = %d";
		String.format(query, trans.getAmount(), trans.getType().toString(), trans.getCategoryId(), trans.getDate(), trans.getReceiptPath(), trans.getTransactionGroupId(), trans.getProfileId(), trans.getNote(), trans.getTransactionId());

		return true;
	}

	public boolean createTransactionGroupDB(TransactionGroup transG) {
		/* I don't know how the relationship between TransactionGroup and Category works 
		   so I omitted it from the query 
		*/
		String query = "INSERT INTO TransactionGroup VALUES (%d, %s, %s, %s)";
		String.format(query, transG.getGroupId(), transG.getName(), transG.getDescription(), transG.getReceiptFilePath());

		return true;
	}

	public boolean deleteTransactionGroupDB(int transGrpID) {
		// replace "id" with name of ID field in DB
		String query = "DELETE FROM TransactionGroup WHERE id = %d";
		String.format(query, transGrpID);

		return true;
	}

	public Transaction getTransaction() { return transaction; }
	public void setTransaction(Transaction t) { transaction = t; }
	public TransactionGroup getTransactionGroup() { return transGroup; }
	public void setTransactionGroup(TransactionGroup g) { transGroup = g; }
}
