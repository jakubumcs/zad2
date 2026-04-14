package cwiczenia.repositories;

import cwiczenia.models.Rental;
import java.util.List;

public interface IRentalRepository {
    void add(Rental rental);
    void update(Rental rental);
    Rental getActiveRentalByUser(String userId);
    Rental getActiveRentalByVehicle(String vehicleId);
    List<Rental> getAllRentals();
    List<Rental> getRentalsByUser(String userId);
}