package cwiczenia.services;

import cwiczenia.models.Vehicle;
import cwiczenia.repositories.IRentalRepository;
import cwiczenia.repositories.IVehicleRepository;

import java.util.List;
import java.util.stream.Collectors;

public class VehicleService {

    private final IVehicleRepository vehicleRepository;
    private final IRentalRepository rentalRepository;
    private final VehicleValidator vehicleValidator;

    public VehicleService(IVehicleRepository vehicleRepository,
                          IRentalRepository rentalRepository,
                          VehicleValidator vehicleValidator) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.vehicleValidator = vehicleValidator;
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        vehicleRepository.add(vehicle);
        return vehicle;
    }

    public boolean removeVehicle(String id) {
        Vehicle v = vehicleRepository.getVehicle(id);
        if (v == null) return false;

        if (rentalRepository.getActiveRentalByVehicle(id) != null) {
            throw new IllegalStateException(
                    "Nie można usunąć pojazdu, bo jest aktualnie wypożyczony.");
        }

        vehicleRepository.remove(id);
        return true;
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.getVehicles();
    }

    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.getVehicles();
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.getVehicles().stream()
                .filter(v -> rentalRepository.getActiveRentalByVehicle(v.getId()) == null)
                .collect(Collectors.toList());
    }

    public List<Vehicle> findAvailableVehicles() {
        return getAvailableVehicles();
    }

    public boolean isVehicleRented(String vehicleId) {
        Vehicle v = vehicleRepository.getVehicle(vehicleId);
        return v != null && v.isRented();
    }

    public Vehicle findById(String id) {
        Vehicle v = vehicleRepository.getVehicle(id);
        if (v == null) throw new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + id);
        return v;
    }
}
