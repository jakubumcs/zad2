package cwiczenia.services;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import cwiczenia.config.LocationProperties;
import cwiczenia.models.Rental;
import cwiczenia.models.User;
import cwiczenia.models.Vehicle;
import cwiczenia.repositories.IRentalRepository;
import cwiczenia.repositories.IUserRepository;
import cwiczenia.repositories.IVehicleRepository;
import cwiczenia.services.interfaces.RentalServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class RentalService implements RentalServiceInterface {

    private final IUserRepository userRepository;
    private final IVehicleRepository vehicleRepository;
    private final IRentalRepository rentalRepository;
    private final LocationProperties locationProperties;

    public RentalService(IUserRepository userRepository,
                         IVehicleRepository vehicleRepository,
                         IRentalRepository rentalRepository,
                         LocationProperties locationProperties) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.locationProperties = locationProperties;
    }

    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        if (rentalRepository.getActiveRentalByUser(userId) != null)
            throw new IllegalStateException("Masz już aktywne wypożyczenie.");

        User user = findUserById(userId);
        Vehicle vehicle = vehicleRepository.getVehicle(vehicleId);
        if (vehicle == null)
            throw new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + vehicleId);
        if (vehicle.isRented())
            throw new IllegalStateException("Pojazd jest już wypożyczony.");

        Rental rental = new Rental(user, vehicle);
        rentalRepository.add(rental);

        vehicle.setRented(true);
        vehicleRepository.update(vehicle);

        return rental;
    }

    @Override
    public Rental returnVehicle(String userId) {
        Rental rental = rentalRepository.getActiveRentalByUser(userId);
        if (rental == null)
            throw new IllegalStateException("Brak aktywnego wypożyczenia.");

        Vehicle vehicle = vehicleRepository.getVehicle(rental.getVehicleId());
        if (vehicle == null)
            throw new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + rental.getVehicleId());

        requireVehicleInAllowedLocation(vehicle);

        rental.setReturnDateTime(LocalDateTime.now());
        rentalRepository.update(rental);

        vehicle.setRented(false);
        vehicleRepository.update(vehicle);

        return rental;
    }

    @Override
    public Map<String, String> payForRental(String rentalId) {
        Rental rental = rentalRepository.getById(rentalId);
        if (rental == null)
            throw new IllegalArgumentException("Nie znaleziono wypożyczenia o ID: " + rentalId);
        if (rental.getReturnDateTime() == null)
            throw new IllegalStateException("Nie można opłacić wypożyczenia, które nie zostało jeszcze zwrócone.");

        Vehicle vehicle = vehicleRepository.getVehicle(rental.getVehicleId());
        if (vehicle == null)
            throw new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + rental.getVehicleId());

        requireVehicleInAllowedLocation(vehicle);

        long hours = Duration.between(rental.getRentDateTime(), rental.getReturnDateTime()).toHours();
        long days = (long) Math.ceil(hours / 24.0);
        if (days <= 0) days = 1;
        double amount = days * vehicle.getPrice();

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(Math.round(amount * 100))
                    .setCurrency("pln")
                    .putMetadata("rentalId", rental.getId())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();
            PaymentIntent intent = PaymentIntent.create(params);
            return Map.of(
                    "clientSecret", intent.getClientSecret(),
                    "amount", String.valueOf(amount),
                    "days", String.valueOf(days)
            );
        } catch (StripeException e) {
            throw new IllegalStateException("Nie udało się zainicjować płatności Stripe.", e);
        }
    }

    private void requireVehicleInAllowedLocation(Vehicle vehicle) {
        if (!locationProperties.isAllowedLocation(vehicle.getLocationName())) {
            throw new IllegalStateException(
                    "Pojazd musi znajdować się w siedzibie firmy lub innej dozwolonej lokalizacji. Aktualna lokalizacja: "
                            + vehicle.getLocationName());
        }
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
