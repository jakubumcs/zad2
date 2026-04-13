package cwiczenia;

import java.util.List;

public interface IVehicleRepository {
    void add(Vehicle vehicle);
    void remove(String id);
    Vehicle getVehicle(String id);
    List<Vehicle> getVehicles();
    void update(Vehicle vehicle);
}