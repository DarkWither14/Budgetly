public class TransactionOperations {
	private TransactionGroup transGroup;

	public boolean createTransactionDB(Transaction trans, DatabaseConnection dbConnection) {
		/* If you look at the fields of Transaction, I excluded transactionCategory in the statement because 
		   a Transaction's Category can already be accessed with categoryId
		*/
		String statement = "INSERT INTO Transaction VALUES (%d, %f, %s, %d, %tF, %s, %d, %d, %s)";
		String.format(statement, trans.getTransactionId(), trans.getAmount(), trans.getType().toString(), trans.getCategoryId(), trans.getDate(), trans.getReceiptPath(), trans.getTransactionGroupId(), trans.getProfileId(), trans.getNote());

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean deleteTransactionDB(int id, DatabaseConnection dbConnection) {
		//											  replace "id" with name of ID field in DB
		String statement = "DELETE FROM Transaction WHERE id = %d";
		String.format(statement, id);

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean updateTransactionDB(Transaction trans, DatabaseConnection dbConnection) {
		//									   replace fields with name of fields in DB
		String statement = "UPDATE Transaction SET amount=%f, type=%s, categoryId=%d, date=%tF, receiptPath=%s, transactionGroupId=%d, profileId=%d, note=%s WHERE id = %d";
		String.format(statement, trans.getAmount(), trans.getType().toString(), trans.getCategoryId(), trans.getDate(), trans.getReceiptPath(), trans.getTransactionGroupId(), trans.getProfileId(), trans.getNote(), trans.getTransactionId());

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean createTransactionGroupDB(TransactionGroup transG, DatabaseConnection dbConnection) {
		/* I don't know how the relationship between TransactionGroup and Category works 
		   so I omitted it from the statement 
		*/
		String statement = "INSERT INTO TransactionGroup VALUES (%d, %s, %s, %s)";
		String.format(statement, transG.getGroupId(), transG.getName(), transG.getDescription(), transG.getReceiptFilePath());

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean deleteTransactionGroupDB(int transGrpID, DatabaseConnection dbConnection) {
		//												   replace "id" with name of ID field in DB
		String statement = "DELETE FROM TransactionGroup WHERE id = %d";
		String.format(statement, transGrpID);

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public TransactionGroup getTransactionGroup() { return transGroup; }
	public void setTransactionGroup(TransactionGroup g) { transGroup = g; }
}
