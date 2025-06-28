import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/online_examination";
    private static final String USER = "root";
    private static final String PASSWORD = "Tirth@123";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // Load MySQL JDBC driver
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
