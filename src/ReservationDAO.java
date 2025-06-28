import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    private Connection connection;

    // Enum for status to avoid typos
    public enum ReservationStatus {
        BOOKED("booked"),
        CANCELLED("cancelled");

        private final String value;

        ReservationStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static boolean isValid(String status) {
            for (ReservationStatus s : ReservationStatus.values()) {
                if (s.getValue().equals(status)) {
                    return true;
                }
            }
            return false;
        }
    }

    public ReservationDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean addReservation(Reservation reservation) throws SQLException {
        String status = reservation.getStatus();
        if (!ReservationStatus.isValid(status)) {
            throw new IllegalArgumentException("Invalid reservation status: " + status);
        }

        String sql = "INSERT INTO reservations (user_id, train_id, journey_date, seat_count, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reservation.getUserId());
            stmt.setInt(2, reservation.getTrainId());
            stmt.setDate(3, new java.sql.Date(reservation.getJourneyDate().getTime()));
            stmt.setInt(4, reservation.getSeatCount());
            stmt.setString(5, status);

            System.out.println("Inserting reservation with status = " + status);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public List<Reservation> getReservationsByUserId(int userId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(new Reservation(
                            rs.getInt("pnr"),
                            rs.getInt("user_id"),
                            rs.getInt("train_id"),
                            rs.getDate("journey_date"),
                            rs.getInt("seat_count"),
                            rs.getString("status"),
                            rs.getTimestamp("booking_time")
                    ));
                }
            }
        }
        return reservations;
    }

    // Fetch reservation by ID (PNR)
    public Reservation getReservationById(int pnr) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE pnr = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pnr);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Reservation(
                            rs.getInt("pnr"),
                            rs.getInt("user_id"),
                            rs.getInt("train_id"),
                            rs.getDate("journey_date"),
                            rs.getInt("seat_count"),
                            rs.getString("status"),
                            rs.getTimestamp("booking_time")
                    );
                } else {
                    return null;
                }
            }
        }
    }

    // Cancel reservation by updating status
    public boolean cancelReservation(int pnr) throws SQLException {
        String sql = "UPDATE reservations SET status = 'cancelled' WHERE pnr = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pnr);
            return stmt.executeUpdate() > 0;
        }
    }

    // Additional methods for update, cancel etc. can be added here
}
