package cwiczenia.web;

import cwiczenia.models.User;
import cwiczenia.models.Vehicle;
import cwiczenia.repositories.IRentalRepository;
import cwiczenia.repositories.IUserRepository;
import cwiczenia.repositories.IVehicleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestResetController {

    private final IVehicleRepository vehicleRepository;
    private final IRentalRepository rentalRepository;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TestResetController(IVehicleRepository vehicleRepository,
                               IRentalRepository rentalRepository,
                               IUserRepository userRepository,
                               PasswordEncoder passwordEncoder) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/reset")
    public Map<String, String> reset() {
        rentalRepository.removeAll();
        vehicleRepository.removeAll();
        userRepository.removeAll();

        userRepository.add(new User(
                "8a2ae9bf-fa64-49a8-8a38-7a3944baac65",
                "admin",
                passwordEncoder.encode("admin123"),
                "ADMIN",
                "ul. Administracyjna 1, Lublin"
        ));
        userRepository.add(new User(
                "dfd103d3-19fa-40be-a3bd-ffaac9e57734",
                "jakub2",
                passwordEncoder.encode("user123"),
                "USER",
                "ul. Testowa 5, Lublin"
        ));

        vehicleRepository.add(Vehicle.builder()
                .id("65b0cdb4-58c7-419a-97bf-811af7f3fe41")
                .category("Car")
                .brand("Audi")
                .model("A4")
                .year(2025)
                .plate("Lu123")
                .price(300.0)
                .attributes(Map.of("fuelType", "Benzyna"))
                .build());
        vehicleRepository.add(Vehicle.builder()
                .id("629ba273-2f15-4976-96b0-305fd009fb6d")
                .category("Motorcycle")
                .brand("Honda")
                .model("CBR1000RR")
                .year(2020)
                .plate("Lu321")
                .price(200.0)
                .attributes(Map.of("licence", "A", "fuelType", "Benzyna"))
                .build());
        vehicleRepository.add(Vehicle.builder()
                .id("a98a0b2b-541d-4629-a11f-3dae492806e3")
                .category("Bus")
                .brand("Ford")
                .model("Glamys")
                .year(2022)
                .plate("Lu333")
                .price(800.0)
                .attributes(Map.of("seats", 7))
                .build());
        vehicleRepository.add(Vehicle.builder()
                .id("a7beeb1c-3cda-4204-aef0-52defba02ab0")
                .category("Car")
                .brand("Toyota")
                .model("Corolla")
                .year(2020)
                .plate("LU12345")
                .price(150.0)
                .attributes(Map.of("fuelType", "petrol"))
                .build());

        return Map.of("status", "reset complete");
    }
}
