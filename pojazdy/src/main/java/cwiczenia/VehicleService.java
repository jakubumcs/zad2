package cwiczenia;

import java.util.List;
import java.util.stream.Collectors;

public class VehicleService {

    private final IVehicleRepository vehicleRepository;

    public VehicleService(IVehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicleRepository.add(vehicle);
    }

    public boolean removeVehicle(String id) {
        Vehicle v = vehicleRepository.getVehicle(id);
        if (v == null) return false;
        vehicleRepository.remove(id);
        return true;
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.getVehicles();
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.getVehicles().stream()
                .filter(v -> !v.isRented())
                .collect(Collectors.toList());
    }
}
