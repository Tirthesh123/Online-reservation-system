import java.sql.Connection;

import java.sql.SQLException;

public class ReservationService {
    private ReservationDAO reservationDAO;

    public ReservationService(Connection connection) {
        this.reservationDAO = new ReservationDAO(connection);
    }

    public Reservation getReservationById(int reservationId) throws SQLException {
        return reservationDAO.getReservationById(reservationId);
    }

    public boolean cancelReservation(int reservationId) throws SQLException {
        return reservationDAO.cancelReservation(reservationId);
    }
}
