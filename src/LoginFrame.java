import javax.swing.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(30, 30, 80, 25);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(110, 30, 130, 25);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(30, 70, 80, 25);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(110, 70, 130, 25);
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(90, 110, 100, 30);
        add(loginButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            UserService service = new UserService();
            User user = service.login(username, password);
            if (user != null) {
                dispose();
                if (user.getRole().equals("admin")) {
                    new AdminFrame(user);
                } else {
                    new StudentFrame(user);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });

        setVisible(true);
    }
}
