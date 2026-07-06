package cwiczenia.web;

import cwiczenia.models.Rental;
import cwiczenia.services.interfaces.RentalServiceInterface;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalServiceInterface rentalService;

    public RentalController(RentalServiceInterface rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping("/rent/{vehicleId}")
    public Rental rentAuthenticated(@PathVariable String vehicleId, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        return rentalService.rentVehicle(userId, vehicleId);
    }

    @PostMapping("/return")
    public Rental returnAuthenticated(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        return rentalService.returnVehicle(userId);
    }

    @GetMapping("/me")
    public List<Rental> myRentals(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        return rentalService.findUserRentals(userId);
    }

    @PostMapping("/{id}/pay")
    public Map<String, String> pay(@PathVariable String id) {
        return rentalService.payForRental(id);
    }

    @GetMapping
    public List<Rental> list() {
        return rentalService.findAllRentals();
    }

    @GetMapping("/users/{userId}")
    public List<Rental> userRentals(@PathVariable String userId, Authentication authentication) {
        requireSelfOrAdmin(userId, authentication);
        return rentalService.findUserRentals(userId);
    }

    @PostMapping("/users/{userId}/rent/{vehicleId}")
    public Rental rent(@PathVariable String userId, @PathVariable String vehicleId, Authentication authentication) {
        requireSelfOrAdmin(userId, authentication);
        return rentalService.rentVehicle(userId, vehicleId);
    }

    @PostMapping("/users/{userId}/return")
    public Rental returnVehicle(@PathVariable String userId, Authentication authentication) {
        requireSelfOrAdmin(userId, authentication);
        return rentalService.returnVehicle(userId);
    }

    private void requireSelfOrAdmin(String userId, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isSelf = authentication.getPrincipal().equals(userId);
        if (!isAdmin && !isSelf) {
            throw new AccessDeniedException("Nie masz dostępu do danych innego użytkownika.");
        }
    }
}
