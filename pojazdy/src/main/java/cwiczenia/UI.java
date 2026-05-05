package cwiczenia;

import cwiczenia.models.Rental;
import cwiczenia.models.User;
import cwiczenia.models.Vehicle;
import cwiczenia.models.VehicleCategoryConfig;
import cwiczenia.services.AuthService;
import cwiczenia.services.RentalService;
import cwiczenia.services.UserService;
import cwiczenia.services.VehicleCategoryConfigService;
import cwiczenia.services.VehicleService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UI {

    private final AuthService authService;
    private final VehicleService vehicleService;
    private final RentalService rentalService;
    private final UserService userService;
    private final VehicleCategoryConfigService categoryConfigService;
    private final Scanner scanner = new Scanner(System.in);

    public UI(AuthService authService,
              VehicleService vehicleService,
              RentalService rentalService,
              UserService userService,
              VehicleCategoryConfigService categoryConfigService) {
        this.authService = authService;
        this.vehicleService = vehicleService;
        this.rentalService = rentalService;
        this.userService = userService;
        this.categoryConfigService = categoryConfigService;
    }

    public void start() {
        while (true) {
            System.out.println("\n=== System Wypożyczalni Pojazdów ===");
            System.out.println("1. Zaloguj");
            System.out.println("2. Zarejestruj");
            System.out.println("0. Koniec");
            System.out.print("Wybierz opcję: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    User loggedUser = login();
                    if (loggedUser != null) {
                        System.out.println("Zalogowano: " + loggedUser.getLogin()
                                + " [" + loggedUser.getRole() + "]");
                        if ("ADMIN".equals(loggedUser.getRole())) adminMenu(loggedUser);
                        else userMenu(loggedUser);
                    } else {
                        System.out.println("Nieprawidłowy login lub hasło.");
                    }
                }
                case "2" -> register();
                case "0" -> { System.out.println("Do widzenia!"); return; }
                default -> System.out.println("Nieprawidłowa opcja.");
            }
        }
    }

    private User login() {
        System.out.println("=== Logowanie ===");
        String login = readText("Login: ");
        String password = readText("Hasło: ");
        return authService.login(login, password);
    }

    private void register() {
        System.out.println("=== Rejestracja ===");
        String login = readText("Podaj login: ");
        String password = readText("Podaj hasło: ");
        User registered = authService.register(login, password);
        if (registered != null) {
            System.out.println("Zarejestrowano pomyślnie! Możesz się zalogować.");
        } else {
            System.out.println("Błąd rejestracji. Login jest już zajęty.");
        }
    }

    private void adminMenu(User loggedUser) {
        while (true) {
            System.out.println("\n=== MENU ADMINA ===");
            System.out.println("1. Pokaż wszystkie pojazdy");
            System.out.println("2. Dodaj pojazd");
            System.out.println("3. Usuń pojazd");
            System.out.println("4. Pokaż użytkowników");
            System.out.println("5. Usuń użytkownika");
            System.out.println("6. Moje dane");
            System.out.println("7. Historia wszystkich wypożyczeń");
            System.out.println("0. Wyloguj");
            System.out.print("Wybierz opcję: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> vehicleService.findAllVehicles().forEach(System.out::println);
                case "2" -> addVehicle();
                case "3" -> deleteVehicle();
                case "4" -> showAllUsers();
                case "5" -> deleteUser(loggedUser);
                case "6" -> showCurrentUserData(loggedUser);
                case "7" -> showRentalHistory();
                case "0" -> { System.out.println("Wylogowano."); return; }
                default -> System.out.println("Nieprawidłowa opcja.");
            }
        }
    }

    private void userMenu(User loggedUser) {
        while (true) {
            System.out.println("\n=== MENU UŻYTKOWNIKA ===");
            System.out.println("1. Dostępne pojazdy");
            System.out.println("2. Wypożycz pojazd");
            System.out.println("3. Zwróć pojazd");
            System.out.println("4. Moje dane");
            System.out.println("5. Moja historia wypożyczeń");
            System.out.println("0. Wyloguj");
            System.out.print("Wybierz opcję: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    List<Vehicle> availableVehicles = vehicleService.findAvailableVehicles();
                    if (availableVehicles.isEmpty()) {
                        System.out.println("Brak dostępnych pojazdów.");
                    } else {
                        availableVehicles.forEach(System.out::println);
                    }
                }
                case "2" -> rentVehicle(loggedUser);
                case "3" -> returnVehicle(loggedUser);
                case "4" -> showCurrentUserData(loggedUser);
                case "5" -> {
                    List<Rental> rentals = rentalService.findUserRentals(loggedUser.getId());
                    if (rentals.isEmpty()) System.out.println("Brak historii wypożyczeń.");
                    else rentals.forEach(this::printRentalDetails);
                }
                case "0" -> { System.out.println("Wylogowano."); return; }
                default -> System.out.println("Nieprawidłowa opcja.");
            }
        }
    }

    private void addVehicle() {
        System.out.println("=== Dodawanie pojazdu ===");
        List<VehicleCategoryConfig> categories = categoryConfigService.findAllCategories();

        if (categories.isEmpty()) {
            System.out.println("Brak skonfigurowanych kategorii. Sprawdź plik categories.json.");
            return;
        }

        System.out.println("Dostępne kategorie:");
        categories.forEach(c -> System.out.println("  - " + c.getCategory()
                + " (atrybuty: " + c.getAttributes() + ")"));

        try {
            String categoryName = readText("Podaj kategorię: ");
            VehicleCategoryConfig config = categoryConfigService.getByCategory(categoryName);

            Vehicle vehicle = new Vehicle(
                    config.getCategory(),
                    readText("Marka: "),
                    readText("Model: "),
                    readInt("Rok produkcji: "),
                    readText("Numer rejestracyjny: "),
                    readDouble("Cena za dobę: ")
            );

            for (Map.Entry<String, String> entry : config.getAttributes().entrySet()) {
                Object value = readAttributeValue(entry.getKey(), entry.getValue());
                vehicle.addAttribute(entry.getKey(), value);
            }

            Vehicle added = vehicleService.addVehicle(vehicle);
            System.out.println("Pojazd dodany pomyślnie. ID: " + added.getId());

        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void deleteVehicle() {
        System.out.print("Podaj ID pojazdu do usunięcia: ");
        String id = scanner.nextLine().trim();
        try {
            boolean removed = vehicleService.removeVehicle(id);
            System.out.println(removed ? "Pojazd usunięty." : "Nie znaleziono pojazdu.");
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void showAllUsers() {
        List<User> users = userService.findAllUsers();
        if (users.isEmpty()) {
            System.out.println("Brak użytkowników.");
            return;
        }

        users.forEach(user -> {
            System.out.println(user);
            List<Rental> rentals = rentalService.findUserRentals(user.getId());

            List<Rental> activeRentals = rentals.stream()
                    .filter(Rental::isActive)
                    .toList();
            List<Rental> completedRentals = rentals.stream()
                    .filter(rental -> !rental.isActive())
                    .toList();

            System.out.println("  Aktywne wypożyczenia:");
            if (activeRentals.isEmpty()) {
                System.out.println("    brak");
            } else {
                activeRentals.forEach(rental -> printRentalDetails(rental, "    "));
            }

            System.out.println("  Historia wypożyczeń:");
            if (completedRentals.isEmpty()) {
                System.out.println("    brak");
            } else {
                completedRentals.forEach(rental -> printRentalDetails(rental, "    "));
            }

            System.out.println("--------------------");
        });
    }

    private void deleteUser(User loggedUser) {
        System.out.print("Podaj ID lub login użytkownika do usunięcia: ");
        String userId = scanner.nextLine().trim();
        try {
            userService.deleteUser(userId, loggedUser.getId());
            System.out.println("Użytkownik usunięty.");
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void showRentalHistory() {
        List<Rental> rentals = rentalService.findAllRentals();
        if (rentals.isEmpty()) {
            System.out.println("Brak historii wypożyczeń.");
            return;
        }
        rentals.forEach(this::printRentalDetails);
    }

    private void rentVehicle(User loggedUser) {
        System.out.print("Podaj ID pojazdu do wypożyczenia: ");
        String id = scanner.nextLine().trim();
        try {
            rentalService.rentVehicle(loggedUser.getId(), id);
            System.out.println("Pojazd został wypożyczony.");
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void returnVehicle(User loggedUser) {
        try {
            Rental r = rentalService.returnVehicle(loggedUser.getId());
            System.out.println("Pojazd zwrócony: " + r);
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void showCurrentUserData(User loggedUser) {
        System.out.println("ID: " + loggedUser.getId()
                + " | Login: " + loggedUser.getLogin()
                + " | Rola: " + loggedUser.getRole());

        rentalService.findActiveRentalByUserId(loggedUser.getId())
                .ifPresentOrElse(
                        rental -> {
                            try {
                                Vehicle v = vehicleService.findById(rental.getVehicleId());
                                System.out.println("Aktualnie wypożyczony: " + v);
                            } catch (Exception e) {
                                System.out.println("Aktualnie wypożyczony pojazd ID: "
                                        + rental.getVehicleId());
                            }
                        },
                        () -> System.out.println("Brak aktywnego wypożyczenia.")
                );
    }

    private String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("To pole nie może być puste!");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readText(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Wpisz poprawną liczbę całkowitą!");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            try {
                double val = Double.parseDouble(readText(prompt));
                if (val >= 0) return val;
                System.out.println("Wartość nie może być ujemna!");
            } catch (NumberFormatException e) {
                System.out.println("Wpisz poprawną liczbę!");
            }
        }
    }

    private boolean readBoolean(String prompt) {
        while (true) {
            String input = readText(prompt).toLowerCase();
            if (input.equals("true") || input.equals("tak")) return true;
            if (input.equals("false") || input.equals("nie")) return false;
            System.out.println("Wpisz 'true' lub 'false'.");
        }
    }

    private Object readAttributeValue(String attrName, String attrType) {
        return switch (attrType.toLowerCase()) {
            case "string"  -> readText(attrName + " (tekst): ");
            case "number"  -> readDouble(attrName + " (liczba): ");
            case "boolean" -> readBoolean(attrName + " (true/false): ");
            case "integer" -> readInt(attrName + " (liczba całkowita): ");
            default -> throw new IllegalArgumentException("Nieznany typ atrybutu: " + attrType);
        };
    }

    private void printRentalDetails(Rental rental) {
        printRentalDetails(rental, "  ");
    }

    private void printRentalDetails(Rental rental, String indent) {
        System.out.println(indent + rental);

        String login = "nieznany";
        try { login = userService.findById(rental.getUserId()).getLogin(); }
        catch (Exception ignored) {}

        String vehicleStr = "nieznany";
        try { vehicleStr = vehicleService.findById(rental.getVehicleId()).toString(); }
        catch (Exception ignored) {}

        System.out.println(indent + "user: " + login);
        System.out.println(indent + "vehicle: " + vehicleStr);
    }
}
