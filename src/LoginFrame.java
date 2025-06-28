import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDAO userDAO;

    public LoginFrame(Connection connection) {
        this.userDAO = new UserDAO(connection);
        this.setTitle("Login");
        this.setSize(350, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        initUI();
    }
    private void openRegisterForm() {
        new RegisterForm();  // Open the RegisterForm on button click
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));  // changed from 3 to 4 rows

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        JButton exitBtn = new JButton("Exit");

        panel.add(loginBtn);
        panel.add(registerBtn);  // add register button next to login
        panel.add(exitBtn);

        this.add(panel);

        loginBtn.addActionListener(e -> loginAction());
        registerBtn.addActionListener(e -> openRegisterForm());  // new method call
        exitBtn.addActionListener(e -> System.exit(0));
    }


    private void loginAction() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Using plain password, no hashing
            User user = userDAO.validateLogin(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); // Close login window

                if ("admin".equalsIgnoreCase(user.getRole())) {
                    new AdminDashboard(user.getUsername()).setVisible(true);
                } else {
                    new UserDashboard(user.getUsername()).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
