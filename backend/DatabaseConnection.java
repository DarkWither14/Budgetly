import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton MySQL connection utility.
 *
 * Partners: call DatabaseConnection.getConnection() inside your DAO classes
 * to get the shared JDBC connection.
 *
 * Credentials are read from environment variables so they are never
 * hard-coded in source control:
 *   BUDGETLY_DB_URL   (default: jdbc:mysql://localhost:3306/budgetly)
 *   BUDGETLY_DB_USER  (default: root)
 *   BUDGETLY_DB_PASS  (default: empty)
 *
 * Set those variables in your shell or in a .env file before running.
 */
public class DatabaseConnection {

    private static final String DB_URL  =
        System.getenv().getOrDefault("BUDGETLY_DB_URL",  "jdbc:mysql://localhost:3306/budgetly");
    private static final String DB_USER =
        System.getenv().getOrDefault("BUDGETLY_DB_USER", "root");
    private static final String DB_PASS =
        System.getenv().getOrDefault("BUDGETLY_DB_PASS", "");

    private static Connection connection = null;

    // Prevent instantiation
    private DatabaseConnection() {}

    /**
     * Returns the shared MySQL connection, opening it if necessary.
     *
     * @return an open JDBC Connection
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        }
        return connection;
    }

    /**
     * Closes the shared connection. Call this on application shutdown.
     */
    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing DB connection: " + e.getMessage());
        }
    }
}
