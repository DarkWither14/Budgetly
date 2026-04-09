public class TransactionOperations {
	private TransactionGroup transGroup;

	public boolean createTransactionDB(Transaction trans) {
		/* If you look at the fields of Transaction, I excluded transactionCategory in the query because 
		   a Transaction's Category can already be accessed with categoryId
		*/
		String query = "INSERT INTO Transaction VALUES (%d, %f, %s, %d, %tF, %s, %d, %d, %s)";
		String.format(query, trans.getTransactionId(), trans.getAmount(), trans.getType().toString(), trans.getCategoryId(), trans.getDate(), trans.getReceiptPath(), trans.getTransactionGroupId(), trans.getProfileId(), trans.getNote());

		return true;
	}

	public boolean deleteTransactionDB(int id) {
		//											  replace "id" with name of ID field in DB
		String query = "DELETE FROM Transaction WHERE id = %d";
		String.format(query, id);

		return true;
	}

	public boolean updateTransactionDB(Transaction trans) {
		//									   replace fields with name of fields in DB
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
		//												   replace "id" with name of ID field in DB
		String query = "DELETE FROM TransactionGroup WHERE id = %d";
		String.format(query, transGrpID);

		return true;
	}

	public TransactionGroup getTransactionGroup() { return transGroup; }
	public void setTransactionGroup(TransactionGroup g) { transGroup = g; }
}
