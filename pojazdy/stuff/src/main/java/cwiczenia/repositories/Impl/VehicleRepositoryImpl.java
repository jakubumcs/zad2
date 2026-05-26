package cwiczenia.repositories.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cwiczenia.db.JsonFileStorage;
import cwiczenia.models.Vehicle;
import cwiczenia.repositories.IVehicleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

// @Repository oznacza implementację repozytorium – Spring tworzy bean i może obsługiwać wyjątki bazodanowe.
// @Profile("json") oznacza, że ten bean jest aktywny TYLKO gdy APP_PROFILE=json (lub brak zmiennej).
// Dzięki temu Spring nie będzie miał konfliktu z repozytoriami jdbc/jpa dla tego samego interfejsu.
@Repository
@Profile("json")
public class VehicleRepositoryImpl implements IVehicleRepository {

    private final List<Vehicle> vehicles = new ArrayList<>();
    private final JsonFileStorage<Vehicle> storage;

    // @Value wstrzykuje wartość z application.yml zamiast hardkodowania nazwy pliku w kodzie.
    // ${carrent.json.vehicles-file} to klucz zdefiniowany w application.yml.
    public VehicleRepositoryImpl(@Value("${carrent.json.vehicles-file}") String fileName) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.storage = new JsonFileStorage<>(mapper, fileName, Vehicle.class);
        load();
    }

    @Override
    public void add(Vehicle vehicle) {
        vehicles.add(vehicle.copy());
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
                vehicles.set(i, vehicle.copy());
                save();
                return;
            }
        }
    }

    @Override
    public Vehicle getVehicle(String id) {
        for (Vehicle v : vehicles)
            if (v.getId().equals(id)) return v.copy();
        return null;
    }

    @Override
    public List<Vehicle> getVehicles() {
        List<Vehicle> copy = new ArrayList<>();
        for (Vehicle vehicle : vehicles) copy.add(vehicle.copy());
        return copy;
    }

    private void save() { storage.save(vehicles); }
    private void load() { vehicles.clear(); vehicles.addAll(storage.load()); }
}
