import java.sql.Timestamp;
import java.util.Date;

public class Reservation {
    private int pnr;
    private int userId;
    private int trainId;
    private Date journeyDate;
    private int seatCount;
    private String status;  // "booked", "cancelled", or "confirmed"
    private Timestamp bookingTime;

    // Full constructor
    public Reservation(int pnr, int userId, int trainId, Date journeyDate, int seatCount, String status, Timestamp bookingTime) {
        this.pnr = pnr;
        this.userId = userId;
        this.trainId = trainId;
        this.journeyDate = journeyDate;
        this.seatCount = seatCount;
        this.status = status;
        this.bookingTime = bookingTime;
    }

    // Getters

    public int getUserId() {
        return userId;
    }

    public int getTrainId() {
        return trainId;
    }

    public Date getJourneyDate() {
        return journeyDate;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public String getStatus() {
        return status;
    }

}
