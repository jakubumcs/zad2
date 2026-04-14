package cwiczenia.repositories.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cwiczenia.models.Rental;
import cwiczenia.repositories.IRentalRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RentalRepository implements IRentalRepository {

    private final List<Rental> rentals = new ArrayList<>();
    private final String FILE_NAME;
    private final ObjectMapper mapper;

    public RentalRepository() { this("rentals.json"); }

    public RentalRepository(String fileName) {
        this.FILE_NAME = fileName;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
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
    public List<Rental> getAllRentals() {
        return new ArrayList<>(rentals);
    }

    @Override
    public List<Rental> getRentalsByUser(String userId) {
        return rentals.stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    private void save() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_NAME), rentals);
        } catch (Exception e) {
            System.err.println("Error saving rentals: " + e.getMessage());
        }
    }

    private void load() {
        rentals.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try {
            Rental[] loaded = mapper.readValue(file, Rental[].class);
            rentals.addAll(Arrays.asList(loaded));
        } catch (Exception e) {
            System.err.println("Error loading rentals: " + e.getMessage());
        }
    }
}