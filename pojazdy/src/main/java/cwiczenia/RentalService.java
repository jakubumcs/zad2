package cwiczenia;

import java.time.LocalDate;
import java.util.UUID;

public class RentalService {

    private final IVehicleRepository vehicleRepository;
    private final IRentalRepository rentalRepository;

    public RentalService(IVehicleRepository vehicleRepository, IRentalRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    public Rental rent(String userLogin, String vehicleId) {
        if (rentalRepository.getActiveRentalByUser(userLogin) != null) return null;
        Vehicle vehicle = vehicleRepository.getVehicle(vehicleId);
        if (vehicle == null || vehicle.isRented()) return null;

        vehicle.setRented(true);
        vehicleRepository.update(vehicle);

        Rental rental = new Rental(UUID.randomUUID().toString(), userLogin, vehicleId,
                LocalDate.now(), null);
        rentalRepository.add(rental);
        return rental;
    }

    public Rental returnVehicle(String userLogin) {
        Rental rental = rentalRepository.getActiveRentalByUser(userLogin);
        if (rental == null) return null;

        Vehicle vehicle = vehicleRepository.getVehicle(rental.getVehicleId());
        if (vehicle != null) {
            vehicle.setRented(false);
            vehicleRepository.update(vehicle);
        }

        rental.setReturnedAt(LocalDate.now());
        rentalRepository.update(rental);
        return rental;
    }
}
