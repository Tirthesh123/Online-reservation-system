import java.sql.*;
import java.util.*;

public class ExamService {
    public List<Question> getQuestions() {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String[] options = {
                        rs.getString("option1"),
                        rs.getString("option2"),
                        rs.getString("option3"),
                        rs.getString("option4")
                };
                list.add(new Question(
                        rs.getInt("question_id"),
                        rs.getString("question_text"),
                        options,
                        rs.getInt("correct_option")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addQuestion(Question q) {
        String sql = "INSERT INTO questions (question_text, option1, option2, option3, option4, correct_option) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, q.getQuestion_text());
            stmt.setString(2, q.getOptions()[0]);
            stmt.setString(3, q.getOptions()[1]);
            stmt.setString(4, q.getOptions()[2]);
            stmt.setString(5, q.getOptions()[3]);
            stmt.setInt(6, q.getCorrectOption());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean deleteQuestion(int questionId) {
        String sql = "DELETE FROM questions WHERE question_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Delete error: " + e.getMessage());
        }
        return false;
    }

}
