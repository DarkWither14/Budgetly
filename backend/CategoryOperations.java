// This needs to be somewhere
//import java.sql.*;

public class CategoryOperations extends DBOperations {
	private Category category;

	@Override
	protected boolean create(Category category) {
		if (category == null || category.getCategoryId() <= 0) {
			throw new IllegalArgumentException("Invalid category or category ID.");
		}
		String query = "INSERT INTO Category VALUES (%d, %s, %s, %s)";
		String formatted = String.format(query, category.getCategoryId(), category.getName(), category.getType().toString(), category.getDescription());
		// TODO: Execute the query using your DB connection
		return true;
	}

	@Override
	protected boolean delete(Category category) {
		if (category == null || category.getCategoryId() <= 0) {
			throw new IllegalArgumentException("Invalid category or category ID.");
		}
		// replace "id" with name of ID field in DB
		String query = "DELETE FROM Category WHERE id = %d";
		String formatted = String.format(query, category.getCategoryId());
		// TODO: Execute the query using your DB connection
		return true;
	}

	public Category getCategory() { return category; }
	public void setCategory(Category c) { category = c; }
}
