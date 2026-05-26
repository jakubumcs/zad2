package cwiczenia.services;

import cwiczenia.models.Rental;
import cwiczenia.models.User;
import cwiczenia.models.Vehicle;
import cwiczenia.repositories.IRentalRepository;
import cwiczenia.repositories.IUserRepository;
import cwiczenia.repositories.IVehicleRepository;
import cwiczenia.services.interfaces.RentalServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RentalService implements RentalServiceInterface {

    private final IUserRepository userRepository;
    private final IVehicleRepository vehicleRepository;
    private final IRentalRepository rentalRepository;

    public RentalService(IUserRepository userRepository,
                         IVehicleRepository vehicleRepository,
                         IRentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        if (rentalRepository.getActiveRentalByUser(userId) != null)
            throw new IllegalStateException("Masz już aktywne wypożyczenie.");

        User user = findUserById(userId);
        Vehicle vehicle = vehicleRepository.getVehicle(vehicleId);
        if (vehicle == null)
            throw new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + vehicleId);
        if (rentalRepository.getActiveRentalByVehicle(vehicleId) != null)
            throw new IllegalStateException("Pojazd jest już wypożyczony.");

        Rental rental = new Rental(user, vehicle);
        rentalRepository.add(rental);
        return rental;
    }

    @Override
    public Rental returnVehicle(String userId) {
        Rental rental = rentalRepository.getActiveRentalByUser(userId);
        if (rental == null)
            throw new IllegalStateException("Brak aktywnego wypożyczenia.");

        rental.setReturnDateTime(LocalDateTime.now());
        rentalRepository.update(rental);
        return rental;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findAllRentals() {
        return rentalRepository.getAllRentals();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findUserRentals(String userId) {
        return rentalRepository.getRentalsByUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return Optional.ofNullable(rentalRepository.getActiveRentalByUser(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasActiveRental(String userId) {
        return rentalRepository.getActiveRentalByUser(userId) != null;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepository.getActiveRentalByVehicle(vehicleId) != null;
    }

    private User findUserById(String userId) {
        return userRepository.getUsers().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika o ID: " + userId));
    }
}
