public class CategoryOperations extends DBOperations<Category> {
    private Category category;
	private DatabaseConnection dbConnection;

    @Override
    protected boolean create(Category category) {
        if (category == null || category.getCategoryId() <= 0)
            throw new IllegalArgumentException("Invalid category or category ID.");
        String query = "INSERT INTO Category VALUES (%d, '%s', '%s', '%s')";
        String formatted = String.format(query, category.getCategoryId(), category.getName(), category.getType().toString(), category.getDescription());
        
		try {
			dbConnection.getConnection().createStatement().executeUpdate(formatted);
			return true;
		} catch (Exception e) {
			return false;
		}
    }

    @Override
    protected boolean delete(Category category) {
        if (category == null || category.getCategoryId() <= 0)
            throw new IllegalArgumentException("Invalid category or category ID.");
        String query = "DELETE FROM Category WHERE id = %d";
        String formatted = String.format(query, category.getCategoryId());
        
		try {
			dbConnection.getConnection().createStatement().executeUpdate(formatted);
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