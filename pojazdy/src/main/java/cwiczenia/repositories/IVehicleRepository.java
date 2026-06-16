package cwiczenia.repositories;

import cwiczenia.models.Vehicle;

import java.util.List;

public interface IVehicleRepository {
    void add(Vehicle vehicle);
    void remove(String id);
    void removeAll();
    Vehicle getVehicle(String id);
    List<Vehicle> getVehicles();
    void update(Vehicle vehicle);
}
