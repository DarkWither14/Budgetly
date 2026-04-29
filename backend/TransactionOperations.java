public class TransactionOperations {
	private TransactionGroup transGroup;
	private DatabaseConnection dbConnection;

	public boolean createTransactionDB(Transaction trans) {
		/* If you look at the fields of Transaction, I excluded transactionCategory in the statement because 
		   a Transaction's Category can already be accessed with categoryId
		*/
		String statement = "INSERT INTO Transaction VALUES (%d, %f, '%s', %d, '%tF', '%s', %d, %d, '%s')";
		statement = String.format(statement, trans.getTransactionId(), trans.getAmount(), trans.getType().toString(), trans.getCategoryId(), trans.getDate(), trans.getReceiptPath(), trans.getTransactionGroupId(), trans.getProfileId(), trans.getNote());

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean deleteTransactionDB(int id) {
		//											  replace "id" with name of ID field in DB
		String statement = "DELETE FROM Transaction WHERE id = %d";
		statement = String.format(statement, id);

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean updateTransactionDB(Transaction trans) {
		//									   replace fields with name of fields in DB
		String statement = "UPDATE Transaction SET amount=%f, type='%s', categoryId=%d, date=%tF, receiptPath='%s', transactionGroupId=%d, profileId=%d, note=%s WHERE id = %d";
		statement = String.format(statement, trans.getAmount(), trans.getType().toString(), trans.getCategoryId(), trans.getDate(), trans.getReceiptPath(), trans.getTransactionGroupId(), trans.getProfileId(), trans.getNote(), trans.getTransactionId());

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean createTransactionGroupDB(TransactionGroup transG) {
		/* I don't know how the relationship between TransactionGroup and Category works 
		   so I omitted it from the statement 
		*/
		String statement = "INSERT INTO TransactionGroup VALUES (%d, '%s', '%s', '%s')";
		statement = String.format(statement, transG.getGroupId(), transG.getName(), transG.getDescription(), transG.getReceiptFilePath());

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean deleteTransactionGroupDB(int transGrpID) {
		//												   replace "id" with name of ID field in DB
		String statement = "DELETE FROM TransactionGroup WHERE id = %d";
		statement = String.format(statement, transGrpID);

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public TransactionGroup getTransactionGroup() { return transGroup; }
	public void setTransactionGroup(TransactionGroup g) { transGroup = g; }

	public DatabaseConnection getDatabaseConnection() { return dbConnection; }
	public void setDatabaseConnection(DatabaseConnection db) { dbConnection = db; }
}
