import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class AdminDashboard extends JFrame {

    private String adminUsername;
    private Connection connection;

    private JTable trainTable;
    private DefaultTableModel trainTableModel;

    private JTable reservationTable;
    private DefaultTableModel reservationTableModel;

    public AdminDashboard(String username) {
        this.adminUsername = username;

        setTitle("Admin Dashboard - Welcome " + adminUsername);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            connection = DBConnection.getConnection(); // Your DB connection utility class
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initUI();
        loadTrains();
        loadReservations();
    }

    private void initUI() {
        // Layout setup
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // Top label
        JLabel welcomeLabel = new JLabel("Admin: " + adminUsername, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Tabbed pane for Trains and Reservations
        JTabbedPane tabbedPane = new JTabbedPane();

        // Trains Tab
        JPanel trainsPanel = new JPanel(new BorderLayout(10, 10));

        // Train Table
        trainTableModel = new DefaultTableModel(new String[]{"ID", "Train Number", "Train Name", "Class Type", "Seats Available"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // disable cell editing
            }
        };
        trainTable = new JTable(trainTableModel);
        JScrollPane trainScrollPane = new JScrollPane(trainTable);

        trainsPanel.add(trainScrollPane, BorderLayout.CENTER);

        // Buttons panel for Train operations
        JPanel trainButtonsPanel = new JPanel();

        JButton addTrainBtn = new JButton("Add Train");
        JButton editTrainBtn = new JButton("Edit Train");
        JButton deleteTrainBtn = new JButton("Delete Train");
        JButton refreshTrainsBtn = new JButton("Refresh");

        trainButtonsPanel.add(addTrainBtn);
        trainButtonsPanel.add(editTrainBtn);
        trainButtonsPanel.add(deleteTrainBtn);
        trainButtonsPanel.add(refreshTrainsBtn);

        trainsPanel.add(trainButtonsPanel, BorderLayout.SOUTH);

        tabbedPane.add("Manage Trains", trainsPanel);

        // Reservations Tab
        JPanel reservationsPanel = new JPanel(new BorderLayout(10, 10));

        reservationTableModel = new DefaultTableModel(new String[]{"PNR", "User", "Train Number", "Train Name", "Journey Date", "Seats", "Status", "Booking Time"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationTable = new JTable(reservationTableModel);
        JScrollPane reservationScrollPane = new JScrollPane(reservationTable);

        reservationsPanel.add(reservationScrollPane, BorderLayout.CENTER);

        JButton refreshReservationsBtn = new JButton("Refresh Reservations");
        reservationsPanel.add(refreshReservationsBtn, BorderLayout.SOUTH);

        tabbedPane.add("View Reservations", reservationsPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);

        // Button actions

        addTrainBtn.addActionListener(e -> openAddTrainDialog());
        editTrainBtn.addActionListener(e -> openEditTrainDialog());
        deleteTrainBtn.addActionListener(e -> deleteSelectedTrain());
        refreshTrainsBtn.addActionListener(e -> loadTrains());
        refreshReservationsBtn.addActionListener(e -> loadReservations());
    }

    // Load all trains from DB into table
    private void loadTrains() {
        trainTableModel.setRowCount(0); // clear existing rows
        String query = "SELECT * FROM trains ORDER BY ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("ID"));
                row.add(rs.getString("train_number"));
                row.add(rs.getString("train_name"));
                row.add(rs.getString("class_type"));
                row.add(rs.getInt("seats_available"));

                trainTableModel.addRow(row);
            }
        } catch (SQLException e) {
            showError("Error loading trains: " + e.getMessage());
        }
    }

    // Load all reservations with joins for readable info
    private void loadReservations() {
        reservationTableModel.setRowCount(0);

        String query = "SELECT r.pnr, u.username, t.train_number, t.train_name, r.journey_date, r.seat_count, r.status, r.booking_time " +
                "FROM reservations r " +
                "JOIN users u ON r.user_id = u.id " +
                "JOIN trains t ON r.train_id = t.id " +
                "ORDER BY r.booking_time DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("pnr"));
                row.add(rs.getString("username"));
                row.add(rs.getString("train_number"));
                row.add(rs.getString("train_name"));
                row.add(rs.getDate("journey_date"));
                row.add(rs.getInt("seat_count"));
                row.add(rs.getString("status"));
                row.add(rs.getTimestamp("booking_time"));

                reservationTableModel.addRow(row);
            }

        } catch (SQLException e) {
            showError("Error loading reservations: " + e.getMessage());
        }
    }

    // Add Train dialog
    private void openAddTrainDialog() {
        TrainDialog dialog = new TrainDialog(this, "Add New Train", null);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadTrains();
        }
    }

    // Edit selected train
    private void openEditTrainDialog() {
        int selectedRow = trainTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a train to edit.");
            return;
        }

        int trainId = (int) trainTableModel.getValueAt(selectedRow, 0);

        Train train = getTrainById(trainId);
        if (train == null) {
            showError("Selected train not found.");
            return;
        }

        TrainDialog dialog = new TrainDialog(this, "Edit Train", train);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadTrains();
        }
    }

    // Delete selected train
    private void deleteSelectedTrain() {
        int selectedRow = trainTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a train to delete.");
            return;
        }

        int trainId = (int) trainTableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this train?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM trains WHERE id = ?")) {
            stmt.setInt(1, trainId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Train deleted successfully.");
                loadTrains();
            } else {
                showError("Failed to delete train.");
            }
        } catch (SQLException e) {
            showError("Error deleting train: " + e.getMessage());
        }
    }

    // Fetch a single train by ID
    private Train getTrainById(int id) {
        String query = "SELECT * FROM trains WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Train(
                            rs.getInt("id"),
                            rs.getString("train_number"),
                            rs.getString("train_name"),
                            rs.getString("class_type"),
                            rs.getInt("seats_available")
                    );
                }
            }
        } catch (SQLException e) {
            showError("Error fetching train: " + e.getMessage());
        }
        return null;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
