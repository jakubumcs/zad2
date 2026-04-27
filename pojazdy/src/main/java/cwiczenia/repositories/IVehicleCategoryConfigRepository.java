package cwiczenia.repositories;

import cwiczenia.models.VehicleCategoryConfig;

import java.util.List;
import java.util.Optional;

public interface IVehicleCategoryConfigRepository {
    List<VehicleCategoryConfig> findAll();
    Optional<VehicleCategoryConfig> findByCategory(String category);
}
