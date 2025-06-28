import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.util.List;

public class ExamFrame extends JFrame {
    private List<Question> questions;
    private int index = 0;
    private int score = 0;
    private User student;
    private Timer timer;
    private JLabel questionLabel, timerLabel;
    private JRadioButton[] options;
    private JButton nextButton;
    private ButtonGroup group;
    private int timeLeft; // in seconds

    public ExamFrame(User student) {
        this.student = student;
        questions = new ExamService().getQuestions();

        setTitle("Exam");
        setSize(550, 350);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        questionLabel = new JLabel();
        questionLabel.setBounds(30, 20, 480, 30);
        add(questionLabel);

        options = new JRadioButton[4];
        group = new ButtonGroup();

        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            options[i].setBounds(30, 60 + i * 30, 480, 30);
            group.add(options[i]);
            add(options[i]);
        }

        nextButton = new JButton("Next");
        nextButton.setBounds(200, 200, 120, 30);
        add(nextButton);

        timerLabel = new JLabel();
        timerLabel.setBounds(400, 10, 120, 20);
        add(timerLabel);

        nextButton.addActionListener(e -> nextQuestion());

        loadQuestion();
        startTimer();

        setVisible(true);
    }

    private void startTimer() {
        int durationInMinutes = new ExamSettingsService().getExamDuration(); // e.g., 5 minutes
        timeLeft = durationInMinutes * 60;

        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int minutes = timeLeft / 60;
                int seconds = timeLeft % 60;
                timerLabel.setText(String.format("⏱ Time: %02d:%02d", minutes, seconds));
                timeLeft--;
                if (timeLeft < 0) {
                    timer.stop();
                    endExam();
                }
            }
        });
        timer.start();
    }

    private void loadQuestion() {
        if (index >= questions.size()) {
            endExam();
            return;
        }

        group.clearSelection();
        Question q = questions.get(index);
        questionLabel.setText((index + 1) + ". " + q.getQuestion_text());
        for (int i = 0; i < 4; i++) {
            options[i].setText(q.getOptions()[i]);
        }
    }

    private void nextQuestion() {
        Question q = questions.get(index);
        for (int i = 0; i < 4; i++) {
            if (options[i].isSelected() && (i + 1) == q.getCorrectOption()) {
                score++;
                break;
            }
        }
        index++;
        loadQuestion();
    }

    private void endExam() {
        if (timer != null) timer.stop();
        dispose();
        new ResultFrame(student, score, questions.size());
    }
}
