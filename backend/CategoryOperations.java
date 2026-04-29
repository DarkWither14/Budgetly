public class CategoryOperations {
	private Category category;
	private DatabaseConnection dbConnection;

	public boolean createCategoryDB(int id, String name) {
		String statement = "INSERT INTO Category VALUES (%d, '%s', '%s', '%s')";
		// Are we using the category variable or the parameters?
		statement = String.format(statement, id, name, category.getType().toString(), category.getDescription());
		
		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean deleteCategoryDB(int id) {
		//								           replace "id" with name of ID field in DB
		String statement = "DELETE FROM Category where id = %d";
		statement = String.format(statement, id);

		try {
			dbConnection.getConnection().createStatement().executeUpdate(statement);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Category getCategory() { return category; }
	public void setCategory(Category c) { category = c; }

	public DatabaseConnection getDatabaseConnection() { return dbConnection; }
	public void setDatabaseConnection(DatabaseConnection db) { dbConnection = db; }
}
