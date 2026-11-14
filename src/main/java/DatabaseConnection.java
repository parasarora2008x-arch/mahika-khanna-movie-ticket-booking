import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DatabaseConnection.java
 * Handles JDBC connection between Java and MySQL Database (movie_ticket_db).
 * 
 * College Practical Project: Movie Ticket Booking Management System
 */
public class DatabaseConnection {

    // =========================================================================
    // EDITABLE DATABASE CONFIGURATION
    // Change these values if your MySQL setup uses a different host, port, or credentials
    // =========================================================================
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "movie_ticket_db";

    // Default connection URL with SSL disabled and UTC timezone for compatibility
    private static String url = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME 
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // MySQL Credentials (edit here if your password is 'root', '1234', empty "", etc.)
    private static String user = "root";
    private static String password = "paras";

    static {
        // Attempt to load external db.properties if provided in the working directory
        try {
            File propFile = new File("db.properties");
            if (propFile.exists()) {
                Properties props = new Properties();
                try (InputStream in = new FileInputStream(propFile)) {
                    props.load(in);
                    if (props.containsKey("db.url")) url = props.getProperty("db.url");
                    if (props.containsKey("db.user")) user = props.getProperty("db.user");
                    if (props.containsKey("db.password")) password = props.getProperty("db.password");
                    System.out.println("[DatabaseConnection] Loaded custom settings from db.properties");
                }
            }
        } catch (Exception e) {
            System.err.println("[DatabaseConnection] Notice: Using built-in credentials: " + e.getMessage());
        }

        // Register MySQL JDBC Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[DatabaseConnection] MySQL JDBC Driver registered successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseConnection] ERROR: MySQL JDBC Driver not found in classpath!");
            e.printStackTrace();
        }
    }

    /**
     * Establishes and returns a new active Connection to the MySQL database.
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            System.err.println("[DatabaseConnection] Failed to connect to MySQL database at: " + url);
            System.err.println("[DatabaseConnection] User: " + user + " | Error: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Helper to test if database connection is alive and working.
     * @return true if connection succeeds, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return (conn != null && !conn.isClosed());
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Getter for current DB URL
     */
    public static String getUrl() {
        return url;
    }

    /**
     * Getter for current DB User
     */
    public static String getUser() {
        return user;
    }

    /**
     * Allows dynamically updating credentials at runtime if user wants to change them from GUI.
     */
    public static void setCredentials(String newUser, String newPassword) {
        user = newUser;
        password = newPassword;
    }
}
