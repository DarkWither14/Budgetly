public abstract class DBOperations<T> {
    protected DatabaseConnection conn = null;
    protected abstract boolean create(T obj);
    protected abstract boolean delete(T obj);
}