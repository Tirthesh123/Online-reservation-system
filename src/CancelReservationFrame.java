import javax.swing.*;
import java.awt.*;

public class CancelReservationFrame extends JFrame {
    private ReservationService reservationService;
    private Runnable onCancelSuccess;

    public CancelReservationFrame(ReservationService reservationService, Runnable onCancelSuccess) {
        this.reservationService = reservationService;
        this.onCancelSuccess = onCancelSuccess;

        setTitle("Cancel Reservation");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTextField pnrField = new JTextField();
        JButton fetchButton = new JButton("Fetch");
        JTextArea detailsArea = new JTextArea();
        JButton cancelButton = new JButton("Cancel");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Enter PNR:"), BorderLayout.WEST);
        inputPanel.add(pnrField, BorderLayout.CENTER);
        inputPanel.add(fetchButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        add(cancelButton, BorderLayout.SOUTH);

        fetchButton.addActionListener(e -> {
            try {
                int pnr = Integer.parseInt(pnrField.getText());
                Reservation r = reservationService.getReservationById(pnr);
                if (r == null) {
                    JOptionPane.showMessageDialog(this, "Reservation not found.");
                } else {
                    detailsArea.setText("Train ID: " + r.getTrainId() +
                            "\nDate: " + r.getJourneyDate() +
                            "\nSeats: " + r.getSeatCount() +
                            "\nStatus: " + r.getStatus());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        cancelButton.addActionListener(e -> {
            try {
                int pnr = Integer.parseInt(pnrField.getText());
                boolean success = reservationService.cancelReservation(pnr);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Cancelled successfully.");
                    if (onCancelSuccess != null) onCancelSuccess.run(); // Callback to refresh UI
                    dispose(); // Close window
                } else {
                    JOptionPane.showMessageDialog(this, "Cancellation failed.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
