import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class TrainDialog extends JDialog {

    private JTextField trainNumberField;
    private JTextField trainNameField;
    private JTextField classTypeField;
    private JTextField seatsAvailableField;

    private boolean succeeded;
    private Train train; // null if adding new

    private Connection connection;

    public TrainDialog(Frame parent, String title, Train train) {
        super(parent, title, true);
        this.train = train;

        try {
            connection = DBConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        initUI();
        if (train != null) {
            loadTrainData();
        }

        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        panel.add(new JLabel("Train Number:"));
        trainNumberField = new JTextField(20);
        panel.add(trainNumberField);

        panel.add(new JLabel("Train Name:"));
        trainNameField = new JTextField(20);
        panel.add(trainNameField);

        panel.add(new JLabel("Class Type:"));
        classTypeField = new JTextField(20);
        panel.add(classTypeField);

        panel.add(new JLabel("Seats Available:"));
        seatsAvailableField = new JTextField(20);
        panel.add(seatsAvailableField);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        panel.add(saveBtn);
        panel.add(cancelBtn);

        add(panel);

        saveBtn.addActionListener(e -> saveTrain());
        cancelBtn.addActionListener(e -> {
            succeeded = false;
            dispose();
        });
    }

    private void loadTrainData() {
        trainNumberField.setText(train.getTrainNumber());
        trainNameField.setText(train.getTrainName());
        classTypeField.setText(train.getClassType());
        seatsAvailableField.setText(String.valueOf(train.getSeatsAvailable()));
    }

    private void saveTrain() {
        String trainNumber = trainNumberField.getText().trim();
        String trainName = trainNameField.getText().trim();
        String classType = classTypeField.getText().trim();
        String seatsStr = seatsAvailableField.getText().trim();

        if (trainNumber.isEmpty() || trainName.isEmpty() || classType.isEmpty() || seatsStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int seatsAvailable;
        try {
            seatsAvailable = Integer.parseInt(seatsStr);
            if (seatsAvailable < 0) {
                throw new NumberFormatException("Seats must be positive.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Seats Available must be a positive integer.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (train == null) {
                // Insert new train
                String sql = "INSERT INTO trains (train_number, train_name, class_type, seats_available) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, trainNumber);
                    stmt.setString(2, trainName);
                    stmt.setString(3, classType);
                    stmt.setInt(4, seatsAvailable);
                    stmt.executeUpdate();
                }
            } else {
                // Update existing train
                String sql = "UPDATE trains SET train_number = ?, train_name = ?, class_type = ?, seats_available = ? WHERE id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, trainNumber);
                    stmt.setString(2, trainName);
                    stmt.setString(3, classType);
                    stmt.setInt(4, seatsAvailable);
                    stmt.setInt(5, train.getId());
                    stmt.executeUpdate();
                }
            }
            succeeded = true;
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving train: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}
