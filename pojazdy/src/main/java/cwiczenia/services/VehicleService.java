package cwiczenia.services;

import cwiczenia.models.Vehicle;
import cwiczenia.repositories.IVehicleRepository;

import java.util.List;
import java.util.stream.Collectors;

public class VehicleService {

    private final IVehicleRepository vehicleRepository;

    public VehicleService(IVehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public void addVehicle(Vehicle vehicle) {
        if (vehicleRepository.getVehicle(vehicle.getId()) == null) {
            vehicleRepository.add(vehicle);
        } else {
            System.err.println("Vehicle with ID " + vehicle.getId() + " already exists.");
        }
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
