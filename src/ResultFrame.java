import javax.swing.*;

public class ResultFrame extends JFrame {
    public ResultFrame(User student, int score, int total) {
        setTitle("Result");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel label = new JLabel("Hi " + student.getName() + ", Your Score: " + score + "/" + total);
        label.setBounds(30, 40, 250, 30);
        add(label);

        setVisible(true);
    }
}
