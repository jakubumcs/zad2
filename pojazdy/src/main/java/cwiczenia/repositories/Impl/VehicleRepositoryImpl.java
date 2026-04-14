package cwiczenia.repositories.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cwiczenia.models.Vehicle;
import cwiczenia.repositories.IVehicleRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VehicleRepositoryImpl implements IVehicleRepository {

    private final List<Vehicle> vehicles = new ArrayList<>();
    private final String FILE_NAME;
    private final ObjectMapper mapper;

    public VehicleRepositoryImpl() { this("vehicles.json"); }

    public VehicleRepositoryImpl(String fileName) {
        this.FILE_NAME = fileName;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        load();
    }

    @Override
    public void add(Vehicle vehicle) {
        vehicles.add(vehicle);
        save();
    }

    @Override
    public void remove(String id) {
        if (vehicles.removeIf(v -> v.getId().equals(id))) save();
    }

    @Override
    public void update(Vehicle vehicle) {
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getId().equals(vehicle.getId())) {
                vehicles.set(i, vehicle);
                save();
                return;
            }
        }
    }

    @Override
    public Vehicle getVehicle(String id) {
        for (Vehicle v : vehicles)
            if (v.getId().equals(id)) return v;
        return null;
    }

    @Override
    public List<Vehicle> getVehicles() {
        return new ArrayList<>(vehicles);
    }

    private void save() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_NAME), vehicles);
        } catch (Exception e) {
            System.err.println("Error saving vehicles: " + e.getMessage());
        }
    }

    private void load() {
        vehicles.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try {
            Vehicle[] loaded = mapper.readValue(file, Vehicle[].class);
            vehicles.addAll(Arrays.asList(loaded));
        } catch (Exception e) {
            System.err.println("Error loading vehicles: " + e.getMessage());
        }
    }
}