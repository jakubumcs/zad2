package cwiczenia.services;

import cwiczenia.models.Rental;
import cwiczenia.models.Vehicle;
import cwiczenia.repositories.IRentalRepository;
import cwiczenia.repositories.IVehicleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RentalService {

    private final IVehicleRepository vehicleRepository;
    private final IRentalRepository rentalRepository;

    public RentalService(IVehicleRepository vehicleRepository, IRentalRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    public Rental rentVehicle(String userId, String vehicleId) {
        if (rentalRepository.getActiveRentalByUser(userId) != null)
            throw new IllegalStateException("Masz już aktywne wypożyczenie.");

        Vehicle vehicle = vehicleRepository.getVehicle(vehicleId);
        if (vehicle == null)
            throw new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + vehicleId);
        if (vehicle.isRented())
            throw new IllegalStateException("Pojazd jest już wypożyczony.");

        vehicle.setRented(true);
        vehicleRepository.update(vehicle);

        Rental rental = new Rental(userId, vehicleId);
        rentalRepository.add(rental);
        return rental;
    }

    public Rental returnVehicle(String userId) {
        Rental rental = rentalRepository.getActiveRentalByUser(userId);
        if (rental == null)
            throw new IllegalStateException("Brak aktywnego wypożyczenia.");

        Vehicle vehicle = vehicleRepository.getVehicle(rental.getVehicleId());
        if (vehicle != null) {
            vehicle.setRented(false);
            vehicleRepository.update(vehicle);
        }

        rental.setReturnDateTime(LocalDateTime.now());
        rentalRepository.update(rental);
        return rental;
    }

    public Rental rent(String userId, String vehicleId) {
        return rentVehicle(userId, vehicleId);
    }

    public Rental returnVehicleCompat(String userId) {
        return returnVehicle(userId);
    }

    public List<Rental> findAllRentals() {
        return rentalRepository.getAllRentals();
    }

    public List<Rental> findUserRentals(String userId) {
        return rentalRepository.getRentalsByUser(userId);
    }

    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return Optional.ofNullable(rentalRepository.getActiveRentalByUser(userId));
    }

    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepository.getActiveRentalByVehicle(vehicleId) != null;
    }
}
