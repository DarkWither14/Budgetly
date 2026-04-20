import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abstraction for a database connection provider.
 *
 * Implementing this interface allows different database backends
 * (MySQL, PostgreSQL, test/in-memory, etc.) to be swapped in without
 * modifying existing code — satisfying the Open/Closed Principle.
 *
 * Use DatabaseConnection connection = MySQLDatabaseConnection.getInstance();
 */
public abstract class DatabaseConnection {

    /**
     * Returns an open database connection.
     *
     * @return an active {@link Connection}
     * @throws SQLException if the connection cannot be established
     */
    public abstract Connection getConnection() throws SQLException;

    /**
     * Closes the underlying connection. Call on application shutdown.
     */
    public abstract void close();
}
