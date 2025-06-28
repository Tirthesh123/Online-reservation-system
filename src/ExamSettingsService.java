import java.sql.*;

public class ExamSettingsService {

    public int getExamDuration() {
        String sql = "SELECT duration_minutes FROM exam_settings WHERE id = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("duration_minutes");
            }
        } catch (SQLException e) {
            System.out.println("Error getting exam duration: " + e.getMessage());
        }
        return 30; // default
    }

    public boolean updateExamDuration(int minutes) {
        String sql = "UPDATE exam_settings SET duration_minutes = ? WHERE id = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, minutes);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating duration: " + e.getMessage());
        }
        return false;
    }
}
