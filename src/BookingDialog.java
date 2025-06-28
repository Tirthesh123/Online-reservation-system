import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class BookingDialog extends JDialog {

    private JComboBox<String> trainComboBox;
    private JTextField journeyDateField; // Format: yyyy-mm-dd
    private JTextField seatCountField;

    private boolean succeeded = false;
    private Connection connection;
    private String username;

    // To map train display strings to IDs
    private Vector<Integer> trainIds = new Vector<>();

    public BookingDialog(Frame parent, String username) {
        super(parent, "Book Ticket", true);
        this.username = username;

        try {
            connection = DBConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        initUI();
        loadTrains();

        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Select Train:"));
        trainComboBox = new JComboBox<>();
        panel.add(trainComboBox);

        panel.add(new JLabel("Journey Date (yyyy-mm-dd):"));
        journeyDateField = new JTextField(15);
        panel.add(journeyDateField);

        panel.add(new JLabel("Number of Seats:"));
        seatCountField = new JTextField(5);
        panel.add(seatCountField);

        JButton bookBtn = new JButton("Book");
        JButton cancelBtn = new JButton("Cancel");

        panel.add(bookBtn);
        panel.add(cancelBtn);

        add(panel);

        bookBtn.addActionListener(e -> bookTicket());
        cancelBtn.addActionListener(e -> {
            succeeded = false;
            dispose();
        });
    }

    private void loadTrains() {
        String sql = "SELECT id, train_number, train_name, seats_available FROM trains WHERE seats_available > 0";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String trainNum = rs.getString("train_number");
                String trainName = rs.getString("train_name");
                int seats = rs.getInt("seats_available");

                trainComboBox.addItem(trainNum + " - " + trainName + " (" + seats + " seats available)");
                trainIds.add(id);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading trains: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bookTicket() {
        int selectedIndex = trainComboBox.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a train.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String journeyDate = journeyDateField.getText().trim();
        String seatCountStr = seatCountField.getText().trim();

        if (journeyDate.isEmpty() || seatCountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int seatCount;
        try {
            seatCount = Integer.parseInt(seatCountStr);
            if (seatCount <= 0) {
                throw new NumberFormatException("Seat count must be positive");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Seat count must be a positive integer.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int trainId = trainIds.get(selectedIndex);

        // Validate journey date format (basic)
        if (!journeyDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Journey date must be in yyyy-mm-dd format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            connection.setAutoCommit(false);

            // Check seats availability
            String seatCheckSql = "SELECT seats_available FROM trains WHERE id = ? FOR UPDATE";
            int availableSeats = 0;
            try (PreparedStatement stmt = connection.prepareStatement(seatCheckSql)) {
                stmt.setInt(1, trainId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        availableSeats = rs.getInt("seats_available");
                    } else {
                        showError("Selected train not found.");
                        connection.setAutoCommit(true);
                        return;
                    }
                }
            }

            if (seatCount > availableSeats) {
                JOptionPane.showMessageDialog(this, "Not enough seats available. Available: " + availableSeats, "Error", JOptionPane.ERROR_MESSAGE);
                connection.setAutoCommit(true);
                return;
            }

            // Get user id
            int userId = -1;
            String getUserSql = "SELECT id FROM users WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(getUserSql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("id");
                    } else {
                        showError("User not found.");
                        connection.setAutoCommit(true);
                        return;
                    }
                }
            }

            // Insert reservation
            String insertSql = "INSERT INTO reservations (user_id, train_id, journey_date, seat_count, status, booking_time) VALUES (?, ?, ?, ?, 'confirmed', CURRENT_TIMESTAMP)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, trainId);
                stmt.setDate(3, Date.valueOf(journeyDate));
                stmt.setInt(4, seatCount);
                stmt.executeUpdate();
            }

            // Update seats_available
            String updateTrainSql = "UPDATE trains SET seats_available = seats_available - ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateTrainSql)) {
                stmt.setInt(1, seatCount);
                stmt.setInt(2, trainId);
                stmt.executeUpdate();
            }

            connection.commit();
            connection.setAutoCommit(true);

            JOptionPane.showMessageDialog(this, "Ticket booked successfully!");
            succeeded = true;
            dispose();

        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) {}
            showError("Error booking ticket: " + e.getMessage());
        }
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
