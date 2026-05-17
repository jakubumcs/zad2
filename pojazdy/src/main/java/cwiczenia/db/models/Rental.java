package cwiczenia.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "rental")
public class Rental {
    @Id
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Vehicle vehicle;

    private LocalDateTime rentDateTime;
    private LocalDateTime returnDateTime;

    public Rental() {}

    public Rental(String userId, String vehicleId) {
        this(createUserReference(userId), createVehicleReference(vehicleId));
    }

    public Rental(User user, Vehicle vehicle) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.vehicle = vehicle;
        this.rentDateTime = LocalDateTime.now();
        this.returnDateTime = null;
    }

    public Rental(String id, String userId, String vehicleId,
                  LocalDateTime rentDateTime, LocalDateTime returnDateTime) {
        this(id, createUserReference(userId), createVehicleReference(vehicleId), rentDateTime, returnDateTime);
    }

    public Rental(String id, User user, Vehicle vehicle,
                  LocalDateTime rentDateTime, LocalDateTime returnDateTime) {
        this.id = id;
        this.user = user;
        this.vehicle = vehicle;
        this.rentDateTime = rentDateTime;
        this.returnDateTime = returnDateTime;
    }

    public String getId() { return id; }
    public User getUser() { return user; }
    public Vehicle getVehicle() { return vehicle; }
    public LocalDateTime getRentDateTime() { return rentDateTime; }
    public LocalDateTime getReturnDateTime() { return returnDateTime; }

    public void setId(String id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public void setRentDateTime(LocalDateTime rentDateTime) { this.rentDateTime = rentDateTime; }
    public void setReturnDateTime(LocalDateTime returnDateTime) { this.returnDateTime = returnDateTime; }

    @Transient
    @JsonProperty("userId")
    public String getUserId() { return user == null ? null : user.getId(); }

    @JsonProperty("userId")
    public void setUserId(String userId) { this.user = createUserReference(userId); }

    @Transient
    @JsonProperty("vehicleId")
    public String getVehicleId() { return vehicle == null ? null : vehicle.getId(); }

    @JsonProperty("vehicleId")
    public void setVehicleId(String vehicleId) { this.vehicle = createVehicleReference(vehicleId); }

    @JsonIgnore
    public boolean isActive() { return returnDateTime == null; }

    @Override
    public String toString() {
        return "Rental[id=" + id + " userId=" + getUserId() + " vehicleId=" + getVehicleId() +
                " from=" + rentDateTime + " to=" + (returnDateTime == null ? "active" : returnDateTime) + "]";
    }

    private static User createUserReference(String userId) {
        if (userId == null || userId.isBlank()) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }

    private static Vehicle createVehicleReference(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return null;
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        return vehicle;
    }
}
