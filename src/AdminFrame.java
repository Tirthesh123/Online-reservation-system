import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminFrame extends JFrame {
    private User admin;
    private ExamService examService = new ExamService();

    public AdminFrame(User admin) {
        this.admin = admin;
        setTitle("Admin Panel");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JButton manageQuestionsBtn = new JButton("Manage Questions");
        manageQuestionsBtn.setBounds(100, 50, 180, 40);
        add(manageQuestionsBtn);

        JButton setTimerBtn = new JButton("Set Exam Timer");
        setTimerBtn.setBounds(100, 110, 180, 40);
        add(setTimerBtn);

        manageQuestionsBtn.addActionListener(e -> openQuestionManager());
        setTimerBtn.addActionListener(e -> {
            String timeStr = JOptionPane.showInputDialog("Enter exam duration (minutes):");
            try {
                int minutes = Integer.parseInt(timeStr);
                if (new ExamSettingsService().updateExamDuration(minutes)) {
                    JOptionPane.showMessageDialog(this, "Timer updated to " + minutes + " minutes.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update timer.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        });

        setVisible(true);
    }

    private void openQuestionManager() {
        JDialog dialog = new JDialog(this, "Manage Questions", true);
        dialog.setSize(700, 400);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        List<Question> questions = examService.getQuestions();
        String[] columnNames = {"ID", "Question", "Option1", "Option2", "Option3", "Option4", "Correct"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (Question q : questions) {
            model.addRow(new Object[]{
                    q.getQuestion_id(),
                    q.getQuestion_text(),
                    q.getOptions()[0],
                    q.getOptions()[1],
                    q.getOptions()[2],
                    q.getOptions()[3],
                    q.getCorrectOption()
            });
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();

        JButton addBtn = new JButton("Add Question");
        JButton deleteBtn = new JButton("Delete Selected");

        addBtn.addActionListener(e -> {
            JTextField questionField = new JTextField(30);
            JTextField[] optionFields = {
                    new JTextField(20),
                    new JTextField(20),
                    new JTextField(20),
                    new JTextField(20)
            };
            JComboBox<String> correctOptionBox = new JComboBox<>(new String[]{"1", "2", "3", "4"});

            JPanel panel = new JPanel(new GridLayout(7, 1, 5, 5));
            panel.add(new JLabel("Enter question:"));
            panel.add(questionField);

            for (int i = 0; i < 4; i++) {
                panel.add(new JLabel("Option " + (i + 1) + ":"));
                panel.add(optionFields[i]);
            }

            panel.add(new JLabel("Correct Option (1-4):"));
            panel.add(correctOptionBox);

            int result = JOptionPane.showConfirmDialog(dialog, panel, "Add New Question",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String questionText = questionField.getText().trim();
                String[] options = new String[4];
                boolean filled = !questionText.isEmpty();

                for (int i = 0; i < 4; i++) {
                    options[i] = optionFields[i].getText().trim();
                    if (options[i].isEmpty()) filled = false;
                }

                int correct = correctOptionBox.getSelectedIndex() + 1;

                if (filled) {
                    Question q = new Question(0, questionText, options, correct);
                    if (examService.addQuestion(q)) {
                        JOptionPane.showMessageDialog(dialog, "✅ Question added.");
                        dialog.dispose();
                        openQuestionManager();  // refresh list
                    } else {
                        JOptionPane.showMessageDialog(dialog, "❌ Failed to add question.");
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "⚠️ All fields must be filled.");
                }
            }
        });


        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) model.getValueAt(row, 0);
                if (examService.deleteQuestion(id)) {
                    model.removeRow(row);
                    JOptionPane.showMessageDialog(dialog, "Question deleted.");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to delete.");
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Select a question to delete.");
            }
        });

        bottomPanel.add(addBtn);
        bottomPanel.add(deleteBtn);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
