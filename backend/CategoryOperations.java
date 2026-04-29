// This needs to be somewhere
//import java.sql.*;

public class CategoryOperations {
	private Category category;

	public boolean createCategoryDB(int id, String name) {
		String query = "INSERT INTO Category VALUES (%d, %s, %s, %s)";
		// Are we using the category variable or the parameters?
		String.format(query, id, name, category.getType().toString(), category.getDescription());
		
		return true;
	}

	public boolean deleteCategoryDB(int id) {
		//								           replace "id" with name of ID field in DB
		String query = "DELETE FROM Category where id = %d";
		String.format(query, id);
		
		return true;
	}

	public Category getCategory() { return category; }
	public void setCategory(Category c) { category = c; }
}
