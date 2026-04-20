public abstract class DBOperations {

    protected DatabaseConnection conn = null;

    protected abstract boolean create(Category category);

    protected abstract boolean delete(Category category);
}
