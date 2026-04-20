import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton implementation of {@link DatabaseConnection} for MySQL.
 *
 * Credentials are read from environment variables so they are never
 * hard-coded in source control:
 *   BUDGETLY_DB_URL   (default: jdbc:mysql://localhost:3306/budgetly)
 *   BUDGETLY_DB_USER  (default: root)
 *   BUDGETLY_DB_PASS  (default: empty)
 *
 * To use a different database backend, implement DatabaseConnection in a
 * new class and swap it in — this class never needs to change.
 *
 * Usage: DatabaseConnection db = MySQLDatabaseConnection.getInstance();
 */
public class MySQLDatabaseConnection extends DatabaseConnection {

    private static final String DB_URL =
            System.getenv().getOrDefault("BUDGETLY_DB_URL",  "jdbc:mysql://localhost:3306/budgetly");
    private static final String DB_USER =
            System.getenv().getOrDefault("BUDGETLY_DB_USER", "root");
    private static final String DB_PASS =
            System.getenv().getOrDefault("BUDGETLY_DB_PASS", "");

    // The single instance — volatile ensures correct behaviour in multi-threaded use
    private static volatile MySQLDatabaseConnection instance = null;

    private Connection connection = null;

    // Private constructor prevents external instantiation
    private MySQLDatabaseConnection() {}

    /**
     * Returns the single instance of this class (Singleton).
     *
     * @return the shared {@link MySQLDatabaseConnection}
     */
    @SuppressWarnings("DoubleCheckedLocking")
    public static MySQLDatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (MySQLDatabaseConnection.class) {
                if (instance == null) {
                    instance = new MySQLDatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Returns an open MySQL connection, opening it if necessary.
     *
     * @return an active {@link Connection}
     * @throws SQLException if the connection cannot be established
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        }
        return connection;
    }

    /**
     * Closes the shared MySQL connection. Call on application shutdown.
     */
    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing DB connection: " + e.getMessage());
        }
    }
}
