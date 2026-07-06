package cwiczenia.services;

import cwiczenia.config.LocationProperties;
import cwiczenia.models.Vehicle;
import cwiczenia.repositories.IVehicleRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class VehicleLocationScheduler {

    private final IVehicleRepository vehicleRepository;
    private final LocationProperties locationProperties;
    private final Random random = new Random();

    public VehicleLocationScheduler(IVehicleRepository vehicleRepository, LocationProperties locationProperties) {
        this.vehicleRepository = vehicleRepository;
        this.locationProperties = locationProperties;
    }

    @Scheduled(fixedRateString = "${carrent.location.scheduler-interval-ms:300000}")
    public void updateRentedVehicleLocations() {
        List<Vehicle> vehicles = vehicleRepository.getVehicles();
        List<LocationProperties.Allowed> outOfBounds = locationProperties.getOutOfBounds();
        List<LocationProperties.Allowed> allowed = locationProperties.getAllowed();
        if (outOfBounds.isEmpty() || allowed.isEmpty()) return;

        for (Vehicle vehicle : vehicles) {
            if (vehicle.isRented()) {
                LocationProperties.Allowed target = outOfBounds.get(random.nextInt(outOfBounds.size()));
                vehicle.setLocation(target.getName(), target.getLatitude(), target.getLongitude());
                vehicleRepository.update(vehicle);
            } else if (!locationProperties.isAllowedLocation(vehicle.getLocationName())) {
                LocationProperties.Allowed home = allowed.get(0);
                vehicle.setLocation(home.getName(), home.getLatitude(), home.getLongitude());
                vehicleRepository.update(vehicle);
            }
        }
    }
}
