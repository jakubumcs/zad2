package cwiczenia.repositories.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import cwiczenia.db.JsonFileStorage;
import cwiczenia.models.VehicleCategoryConfig;
import cwiczenia.repositories.IVehicleCategoryConfigRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VehicleCategoryConfigRepository implements IVehicleCategoryConfigRepository {

    private final List<VehicleCategoryConfig> configs = new ArrayList<>();
    private final JsonFileStorage<VehicleCategoryConfig> storage;

    public VehicleCategoryConfigRepository(@Value("${carrent.json.categories-file}") String fileName) {
        this.storage = new JsonFileStorage<>(new ObjectMapper(), fileName, VehicleCategoryConfig.class);
        load();
    }

    @Override
    public List<VehicleCategoryConfig> findAll() {
        List<VehicleCategoryConfig> copy = new ArrayList<>();
        for (VehicleCategoryConfig c : configs) copy.add(c.copy());
        return copy;
    }

    @Override
    public Optional<VehicleCategoryConfig> findByCategory(String category) {
        return configs.stream()
                .filter(c -> c.getCategory() != null &&
                             c.getCategory().equalsIgnoreCase(category))
                .findFirst()
                .map(VehicleCategoryConfig::copy);
    }

    private void load() {
        configs.clear();
        configs.addAll(storage.load());
        if (configs.isEmpty()) {
            System.err.println("Warning: categories not loaded from " + storage.resolvedPath());
        }
    }
}
