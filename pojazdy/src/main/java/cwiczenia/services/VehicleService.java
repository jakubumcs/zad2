package cwiczenia.services;

import cwiczenia.models.Vehicle;
import cwiczenia.repositories.IRentalRepository;
import cwiczenia.repositories.IVehicleRepository;
import cwiczenia.services.interfaces.VehicleServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleService implements VehicleServiceInterface {

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

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
        }
        vehicleValidator.validate(vehicle);
        vehicleRepository.add(vehicle);
        return vehicle;
    }

    @Override
    public void removeVehicle(String id) {
        Vehicle v = vehicleRepository.getVehicle(id);
        if (v == null) throw new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + id);

        if (v.isRented()) {
            throw new IllegalStateException(
                    "Nie można usunąć pojazdu, bo jest aktualnie wypożyczony.");
        }

        vehicleRepository.remove(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.getVehicles();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.getVehicles().stream()
                .filter(v -> !v.isRented())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicle findById(String id) {
        Vehicle v = vehicleRepository.getVehicle(id);
        if (v == null) throw new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + id);
        return v;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVehicleRented(String vehicleId) {
        Vehicle v = vehicleRepository.getVehicle(vehicleId);
        return v != null && v.isRented();
    }

    @Override
    public Vehicle setLocation(String vehicleId, String locationName, double latitude, double longitude) {
        Vehicle vehicle = vehicleRepository.getVehicle(vehicleId);
        if (vehicle == null) throw new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + vehicleId);
        vehicle.setLocation(locationName, latitude, longitude);
        vehicleRepository.update(vehicle);
        return vehicle;
    }
}
