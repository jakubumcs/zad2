package cwiczenia.web;

import cwiczenia.models.Rental;
import cwiczenia.services.interfaces.RentalServiceInterface;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalServiceInterface rentalService;

    public RentalController(RentalServiceInterface rentalService) {
        this.rentalService = rentalService;
    }

    // GET /api/rentals → historia wszystkich wypożyczeń
    @GetMapping
    public List<Rental> list() {
        return rentalService.findAllRentals();
    }

    // GET /api/rentals/users/{userId} → wypożyczenia konkretnego użytkownika
    @GetMapping("/users/{userId}")
    public List<Rental> userRentals(@PathVariable String userId) {
        return rentalService.findUserRentals(userId);
    }

    // POST /api/rentals/users/{userId}/rent/{vehicleId} → wypożycz pojazd
    // Oba ID przychodzą jako fragmenty URL, bez ciała żądania
    @PostMapping("/users/{userId}/rent/{vehicleId}")
    public Rental rent(@PathVariable String userId, @PathVariable String vehicleId) {
        return rentalService.rentVehicle(userId, vehicleId);
    }

    // POST /api/rentals/users/{userId}/return → zwróć aktywnie wypożyczony pojazd
    @PostMapping("/users/{userId}/return")
    public Rental returnVehicle(@PathVariable String userId) {
        return rentalService.returnVehicle(userId);
    }
}
