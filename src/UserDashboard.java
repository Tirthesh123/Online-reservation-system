import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class UserDashboard extends JFrame {

    private String username;
    private Connection connection;
    private ReservationService reservationService;

    private JTable reservationTable;
    private DefaultTableModel reservationTableModel;

    public UserDashboard(String username) {
        this.username = username;

        setTitle("User Dashboard - Welcome " + username);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            connection = DBConnection.getConnection();
            reservationService = new ReservationService(connection);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initUI();
        loadReservations();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10,10));

        JLabel welcomeLabel = new JLabel("User: " + username, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Reservation Table
        reservationTableModel = new DefaultTableModel(new String[]{"PNR", "Train Number", "Train Name", "Journey Date", "Seats", "Status", "Booking Time"}, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        reservationTable = new JTable(reservationTableModel);
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton bookTicketBtn = new JButton("Book Ticket");
        JButton cancelReservationBtn = new JButton("Cancel Reservation");
        JButton refreshBtn = new JButton("Refresh");

        buttonPanel.add(bookTicketBtn);
        buttonPanel.add(cancelReservationBtn);
        buttonPanel.add(refreshBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        bookTicketBtn.addActionListener(e -> openBookingDialog());
        cancelReservationBtn.addActionListener(e -> openCancelReservationFrame());
        refreshBtn.addActionListener(e -> loadReservations());
    }

    private void loadReservations() {
        reservationTableModel.setRowCount(0);
        String query = "SELECT r.pnr, t.train_number, t.train_name, r.journey_date, r.seat_count, r.status, r.booking_time " +
                "FROM reservations r " +
                "JOIN users u ON r.user_id = u.id " +
                "JOIN trains t ON r.train_id = t.id " +
                "WHERE u.username = ? " +
                "ORDER BY r.booking_time DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("pnr"));
                    row.add(rs.getString("train_number"));
                    row.add(rs.getString("train_name"));
                    row.add(rs.getDate("journey_date"));
                    row.add(rs.getInt("seat_count"));
                    row.add(rs.getString("status"));
                    row.add(rs.getTimestamp("booking_time"));

                    reservationTableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            showError("Error loading reservations: " + e.getMessage());
        }
    }

    private void openBookingDialog() {
        BookingDialog dialog = new BookingDialog(this, username);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadReservations();
        }
    }

    private void openCancelReservationFrame() {
        CancelReservationFrame cancelFrame = new CancelReservationFrame(reservationService, this::loadReservations);
        cancelFrame.setVisible(true);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
