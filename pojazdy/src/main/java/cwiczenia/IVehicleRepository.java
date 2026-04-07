package cwiczenia;

import java.util.List;

public interface IVehicleRepository {

    boolean rentVehicle(String id);

    boolean returnVehicle(String id);

    List<Vehicle> getVehicles();


    void add(Vehicle vehicle);
    void remove(String id);
    Vehicle getVehicle(String id);
}