public class Train {
    private int id;
    private String trainNumber;
    private String trainName;
    private String classType;
    private int seatsAvailable;

    public Train(int id, String trainNumber, String trainName, String classType, int seatsAvailable) {
        this.id = id;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.classType = classType;
        this.seatsAvailable = seatsAvailable;
    }

    // Getters
    public int getId() { return id; }
    public String getTrainNumber() { return trainNumber; }
    public String getTrainName() { return trainName; }
    public String getClassType() { return classType; }
    public int getSeatsAvailable() { return seatsAvailable; }
}
