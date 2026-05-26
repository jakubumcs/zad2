package cwiczenia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication robi trzy rzeczy naraz:
// 1. Włącza autokonfigurację (Spring sam wykrywa co skonfigurować)
// 2. Skanuje komponenty – znajduje klasy z @Service, @Repository, @RestController itp.
// 3. Oznacza tę klasę jako główną konfigurację aplikacji
@SpringBootApplication
public class CarRentApplication {

    public static void main(String[] args) {
        // SpringApplication.run uruchamia cały kontener Springa i serwer HTTP
        SpringApplication.run(CarRentApplication.class, args);
    }
}
