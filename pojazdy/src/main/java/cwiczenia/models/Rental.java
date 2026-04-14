package cwiczenia.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rental {
    private String id;
    private String userId;
    private String vehicleId;
    private LocalDateTime rentDateTime;
    private LocalDateTime returnDateTime;

    public Rental() {}

    public Rental(String userId, String vehicleId) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.rentDateTime = LocalDateTime.now();
        this.returnDateTime = null;
    }

    public Rental(String id, String userId, String vehicleId,
                  LocalDateTime rentDateTime, LocalDateTime returnDateTime) {
        this.id = id;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.rentDateTime = rentDateTime;
        this.returnDateTime = returnDateTime;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getVehicleId() { return vehicleId; }
    public LocalDateTime getRentDateTime() { return rentDateTime; }
    public LocalDateTime getReturnDateTime() { return returnDateTime; }

    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    public void setRentDateTime(LocalDateTime rentDateTime) { this.rentDateTime = rentDateTime; }
    public void setReturnDateTime(LocalDateTime returnDateTime) { this.returnDateTime = returnDateTime; }

    @JsonIgnore
    public boolean isActive() { return returnDateTime == null; }

    @Override
    public String toString() {
        return "Rental[id=" + id + " userId=" + userId + " vehicleId=" + vehicleId +
                " from=" + rentDateTime + " to=" + (returnDateTime == null ? "active" : returnDateTime) + "]";
    }
}
