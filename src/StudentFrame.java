import javax.swing.*;

public class StudentFrame extends JFrame {
    private User student;

    public StudentFrame(User student) {
        this.student = student;

        setTitle("Student Panel");
        setSize(340, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JButton takeExamBtn = new JButton("Start Exam");
        takeExamBtn.setBounds(100, 30, 140, 40);
        add(takeExamBtn);

        JButton updateProfileBtn = new JButton("Update Profile");
        updateProfileBtn.setBounds(100, 90, 140, 40);
        add(updateProfileBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(100, 150, 140, 30);
        add(logoutBtn);

        takeExamBtn.addActionListener(e -> {
            dispose();
            new ExamFrame(student);
        });

        updateProfileBtn.addActionListener(e -> {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JTextField nameField = new JTextField(student.getName(), 20);
            JTextField emailField = new JTextField(student.getEmail(), 20);

            panel.add(new JLabel("New Name:"));
            panel.add(nameField);
            panel.add(Box.createVerticalStrut(10)); // Spacer
            panel.add(new JLabel("New Email:"));
            panel.add(emailField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Update Profile",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();

                if (!name.isEmpty() && !email.isEmpty()) {
                    student.setName(name);
                    student.setEmail(email);
                    if (new UserService().updateProfile(student)) {
                        JOptionPane.showMessageDialog(this, "✅ Profile updated successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "❌ Failed to update profile.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "⚠️ Both fields are required.");
                }
            }
        });


        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        setVisible(true);
    }
}
