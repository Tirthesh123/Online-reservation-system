import java.sql.Connection;
import java.sql.SQLException;

public class UserService {
    private UserDAO userDAO;

    public UserService(Connection connection) {
        this.userDAO = new UserDAO(connection);
    }

    public User login(String username, String password) throws SQLException {
        // No hashing, use plain password directly
        return userDAO.validateLogin(username, password);
    }
    public boolean register(String username, String password) throws SQLException {
        return userDAO.registerUser(username, password);
    }


    // Add registration, password reset, etc.
}
