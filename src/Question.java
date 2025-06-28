public class Question {
    private int question_id;
    private String question_text;
    private String[] options;
    private int correctOption;

    public Question(int question_id, String question_text, String[] options, int correctOption) {
        this.question_id = question_id;
        this.question_text = question_text;
        this.options = options;
        this.correctOption = correctOption;
    }

    public int getQuestion_id() { return question_id; }
    public String getQuestion_text() { return question_text; }
    public String[] getOptions() { return options; }
    public int getCorrectOption() { return correctOption; }
}
