import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Establish database connection
                Connection connection = DBConnection.getConnection();

                // Open the login screen
                LoginFrame loginFrame = new LoginFrame(connection);
                loginFrame.setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                        "Failed to connect to the database:\n" + e.getMessage(),
                        "Database Connection Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
