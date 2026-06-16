package cwiczenia.repositories.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cwiczenia.db.JsonFileStorage;
import cwiczenia.models.Rental;
import cwiczenia.repositories.IRentalRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Profile("json")
public class RentalRepository implements IRentalRepository {

    private final List<Rental> rentals = new ArrayList<>();
    private final JsonFileStorage<Rental> storage;

    public RentalRepository(@Value("${carrent.json.rentals-file}") String fileName) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.storage = new JsonFileStorage<>(mapper, fileName, Rental.class);
        load();
    }

    @Override
    public void add(Rental rental) {
        rentals.add(rental);
        save();
    }

    @Override
    public void update(Rental rental) {
        for (int i = 0; i < rentals.size(); i++) {
            if (rentals.get(i).getId().equals(rental.getId())) {
                rentals.set(i, rental);
                save();
                return;
            }
        }
    }

    @Override
    public void removeAll() {
        rentals.clear();
        save();
    }

    @Override
    public Rental getActiveRentalByUser(String userId) {
        for (Rental r : rentals)
            if (r.getUserId().equals(userId) && r.isActive()) return r;
        return null;
    }

    @Override
    public Rental getActiveRentalByVehicle(String vehicleId) {
        for (Rental r : rentals)
            if (r.getVehicleId().equals(vehicleId) && r.isActive()) return r;
        return null;
    }

    @Override
    public List<Rental> getAllRentals() { return new ArrayList<>(rentals); }

    @Override
    public List<Rental> getRentalsByUser(String userId) {
        return rentals.stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    private void save() { storage.save(rentals); }
    private void load() { rentals.clear(); rentals.addAll(storage.load()); }
}
