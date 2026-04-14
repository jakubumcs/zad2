package cwiczenia.services;

import cwiczenia.models.Rental;
import cwiczenia.models.Vehicle;
import cwiczenia.repositories.IRentalRepository;
import cwiczenia.repositories.IVehicleRepository;

import java.time.LocalDateTime;

public class RentalService {

    private final IVehicleRepository vehicleRepository;
    private final IRentalRepository rentalRepository;

    public RentalService(IVehicleRepository vehicleRepository, IRentalRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    public Rental rent(String userId, String vehicleId) {
        if (rentalRepository.getActiveRentalByUser(userId) != null) return null;
        Vehicle vehicle = vehicleRepository.getVehicle(vehicleId);
        if (vehicle == null || vehicle.isRented()) return null;

        vehicle.setRented(true);
        vehicleRepository.update(vehicle);

        Rental rental = new Rental(userId, vehicleId);
        rentalRepository.add(rental);
        return rental;
    }

    public Rental returnVehicle(String userId) {
        Rental rental = rentalRepository.getActiveRentalByUser(userId);
        if (rental == null) return null;

        Vehicle vehicle = vehicleRepository.getVehicle(rental.getVehicleId());
        if (vehicle != null) {
            vehicle.setRented(false);
            vehicleRepository.update(vehicle);
        }

        rental.setReturnDateTime(LocalDateTime.now());
        rentalRepository.update(rental);
        return rental;
    }
}