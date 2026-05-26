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

// @RestController = @Controller + @ResponseBody
// Każda metoda automatycznie serializuje zwracany obiekt do JSON.
// @RequestMapping ustawia bazowy prefiks URL dla wszystkich metod w tej klasie.
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    // Controller nie zawiera logiki – deleguje wszystko do serwisu.
    // Serwis jest wstrzykiwany przez konstruktor (Spring sam go dostarcza).
    private final VehicleServiceInterface vehicleService;

    public VehicleController(VehicleServiceInterface vehicleService) {
        this.vehicleService = vehicleService;
    }

    // GET /api/vehicles          → wszystkie pojazdy
    // GET /api/vehicles?available=true → tylko dostępne
    @GetMapping
    public List<Vehicle> list(
            @RequestParam(name = "available", required = false, defaultValue = "false") boolean available
    ) {
        return available
                ? vehicleService.findAvailableVehicles()
                : vehicleService.findAllVehicles();
    }

    // GET /api/vehicles/{id} → jeden pojazd po ID
    // @PathVariable pobiera wartość z fragmentu URL (np. /api/vehicles/abc123)
    @GetMapping("/{id}")
    public Vehicle get(@PathVariable String id) {
        return vehicleService.findById(id);
    }

    // POST /api/vehicles – dodanie nowego pojazdu
    // @RequestBody deserializuje JSON z ciała żądania do obiektu Vehicle
    @PostMapping
    public Vehicle create(@RequestBody Vehicle vehicle) {
        return vehicleService.addVehicle(vehicle);
    }

    // DELETE /api/vehicles/{id} – usunięcie pojazdu
    // ResponseEntity<Void> pozwala zwrócić odpowiedź bez ciała z kodem 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        vehicleService.removeVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
