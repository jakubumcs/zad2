package cwiczenia.services.interfaces;

import cwiczenia.models.Vehicle;

import java.util.List;

public interface VehicleServiceInterface {
    List<Vehicle> findAllVehicles();
    List<Vehicle> findAvailableVehicles();
    Vehicle findById(String id);
    Vehicle addVehicle(Vehicle vehicle);
    void removeVehicle(String vehicleId);
    boolean isVehicleRented(String vehicleId);
    Vehicle setLocation(String vehicleId, String locationName, double latitude, double longitude);
}
