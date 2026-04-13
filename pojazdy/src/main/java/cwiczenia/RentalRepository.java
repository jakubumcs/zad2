package cwiczenia;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RentalRepository implements IRentalRepository {

    private final List<Rental> rentals = new ArrayList<>();
    private final String FILE_NAME;

    public RentalRepository() { this("rentals.csv"); }

    public RentalRepository(String fileName) {
        this.FILE_NAME = fileName;
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
    public Rental getActiveRentalByUser(String userLogin) {
        for (Rental r : rentals)
            if (r.getUserLogin().equals(userLogin) && r.isActive()) return r;
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
    public List<Rental> getRentalsByUser(String userLogin) {
        return rentals.stream()
                .filter(r -> r.getUserLogin().equals(userLogin))
                .collect(Collectors.toList());
    }

    private void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Rental r : rentals) writer.println(r.toCSV());
        } catch (IOException e) {
            System.err.println("Error saving rentals: " + e.getMessage());
        }
    }

    private void load() {
        rentals.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length < 5) continue;
                LocalDate returnedAt = parts[4].equals("null") ? null : LocalDate.parse(parts[4]);
                rentals.add(new Rental(parts[0], parts[1], parts[2],
                        LocalDate.parse(parts[3]), returnedAt));
            }
        } catch (IOException e) {
            System.err.println("Error loading rentals: " + e.getMessage());
        }
    }
}