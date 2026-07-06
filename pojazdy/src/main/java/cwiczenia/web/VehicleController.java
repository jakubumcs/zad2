package cwiczenia.web;

import cwiczenia.models.Vehicle;
import cwiczenia.services.interfaces.VehicleServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleServiceInterface vehicleService;

    public VehicleController(VehicleServiceInterface vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<Vehicle> list(
            @RequestParam(name = "available", required = false, defaultValue = "false") boolean available
    ) {
        return available
                ? vehicleService.findAvailableVehicles()
                : vehicleService.findAllVehicles();
    }

    @GetMapping("/{id}")
    public Vehicle get(@PathVariable String id) {
        return vehicleService.findById(id);
    }

    @PostMapping
    public Vehicle create(@RequestBody Vehicle vehicle) {
        return vehicleService.addVehicle(vehicle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        vehicleService.removeVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/location")
    public Vehicle setLocation(@PathVariable String id, @RequestBody Map<String, Object> body) {
        String locationName = (String) body.get("locationName");
        double latitude = body.get("latitude") == null ? 0.0 : ((Number) body.get("latitude")).doubleValue();
        double longitude = body.get("longitude") == null ? 0.0 : ((Number) body.get("longitude")).doubleValue();
        return vehicleService.setLocation(id, locationName, latitude, longitude);
    }
}
