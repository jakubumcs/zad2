package cwiczenia.services;

import cwiczenia.models.VehicleCategoryConfig;
import cwiczenia.repositories.IVehicleCategoryConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleCategoryConfigService {

    private final IVehicleCategoryConfigRepository configRepository;

    public VehicleCategoryConfigService(IVehicleCategoryConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public List<VehicleCategoryConfig> findAllCategories() {
        return configRepository.findAll();
    }

    public VehicleCategoryConfig getByCategory(String category) {
        return configRepository.findByCategory(category)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nieznana kategoria pojazdu: " + category));
    }

    public boolean categoryExists(String category) {
        return configRepository.findByCategory(category).isPresent();
    }
}
