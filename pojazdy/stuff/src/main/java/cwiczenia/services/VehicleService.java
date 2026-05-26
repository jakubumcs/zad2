package cwiczenia.services;

import cwiczenia.models.Vehicle;
import cwiczenia.repositories.IRentalRepository;
import cwiczenia.repositories.IVehicleRepository;
import cwiczenia.services.interfaces.VehicleServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// @Service oznacza tę klasę jako komponent Springa zawierający logikę biznesową.
// Spring automatycznie tworzy jeden egzemplarz tej klasy (bean) i wstrzykuje go
// wszędzie tam, gdzie coś zależy od VehicleServiceInterface.
@Service
// @Transactional na klasie oznacza, że każda metoda domyślnie działa w transakcji bazodanowej.
// Jeśli metoda wyrzuci wyjątek, transakcja zostanie cofnięta (rollback).
// Dla profilu json transakcje nie mają efektu, ale adnotacja nie przeszkadza.
@Transactional
public class VehicleService implements VehicleServiceInterface {

    private final IVehicleRepository vehicleRepository;
    private final IRentalRepository rentalRepository;
    private final VehicleValidator vehicleValidator;

    // Spring wstrzykuje zależności przez konstruktor - nie tworzymy obiektów przez new.
    // Spring sam znajdzie odpowiednie beany pasujące do typów parametrów.
    public VehicleService(IVehicleRepository vehicleRepository,
                          IRentalRepository rentalRepository,
                          VehicleValidator vehicleValidator) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.vehicleValidator = vehicleValidator;
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        vehicleRepository.add(vehicle);
        return vehicle;
    }

    @Override
    public void removeVehicle(String id) {
        Vehicle v = vehicleRepository.getVehicle(id);
        if (v == null) throw new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + id);

        if (rentalRepository.getActiveRentalByVehicle(id) != null) {
            throw new IllegalStateException(
                    "Nie można usunąć pojazdu, bo jest aktualnie wypożyczony.");
        }

        vehicleRepository.remove(id);
    }

    @Override
    // @Transactional(readOnly = true) optymalizuje transakcję dla operacji tylko do odczytu
    // (brak blokad zapisu, szybsze działanie z bazą danych)
    @Transactional(readOnly = true)
    public List<Vehicle> findAllVehicles() {
        return withRentalStatus(vehicleRepository.getVehicles());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAvailableVehicles() {
        return withRentalStatus(vehicleRepository.getVehicles()).stream()
                .filter(v -> rentalRepository.getActiveRentalByVehicle(v.getId()) == null)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicle findById(String id) {
        Vehicle v = vehicleRepository.getVehicle(id);
        if (v == null) throw new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + id);
        return withRentalStatus(v);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVehicleRented(String vehicleId) {
        return rentalRepository.getActiveRentalByVehicle(vehicleId) != null;
    }

    private List<Vehicle> withRentalStatus(List<Vehicle> vehicles) {
        return vehicles.stream()
                .map(this::withRentalStatus)
                .collect(Collectors.toList());
    }

    private Vehicle withRentalStatus(Vehicle vehicle) {
        vehicle.setRented(rentalRepository.getActiveRentalByVehicle(vehicle.getId()) != null);
        return vehicle;
    }
}
