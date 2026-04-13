package cwiczenia;

import java.time.LocalDate;

public class Rental {
    private String id;
    private String userLogin;
    private String vehicleId;
    private LocalDate rentedAt;
    private LocalDate returnedAt;

    public Rental(String id, String userLogin, String vehicleId, LocalDate rentedAt, LocalDate returnedAt) {
        this.id = id;
        this.userLogin = userLogin;
        this.vehicleId = vehicleId;
        this.rentedAt = rentedAt;
        this.returnedAt = returnedAt;
    }

    public String getId() { return id; }
    public String getUserLogin() { return userLogin; }
    public String getVehicleId() { return vehicleId; }
    public LocalDate getRentedAt() { return rentedAt; }
    public LocalDate getReturnedAt() { return returnedAt; }
    public void setReturnedAt(LocalDate returnedAt) { this.returnedAt = returnedAt; }
    public boolean isActive() { return returnedAt == null; }

    public String toCSV() {
        return id + ";" + userLogin + ";" + vehicleId + ";" + rentedAt + ";" +
                (returnedAt == null ? "null" : returnedAt);
    }

    @Override
    public String toString() {
        return "Rental[id=" + id + " user=" + userLogin + " vehicle=" + vehicleId +
                " from=" + rentedAt + " to=" + (returnedAt == null ? "active" : returnedAt) + "]";
    }
}
