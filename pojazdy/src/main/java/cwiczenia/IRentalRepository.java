package cwiczenia;

import java.util.List;

public interface IRentalRepository {
    void add(Rental rental);
    void update(Rental rental);
    Rental getActiveRentalByUser(String userLogin);
    Rental getActiveRentalByVehicle(String vehicleId);
    List<Rental> getAllRentals();
    List<Rental> getRentalsByUser(String userLogin);
}