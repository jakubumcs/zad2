package cwiczenia;

import java.util.Scanner;

public class app {

    public static void main(String[] args) {
        VehicleRepositoryImpl vehicleRepo = new VehicleRepositoryImpl();
        UserRepository userRepo = new UserRepository();
        RentalRepository rentalRepo = new RentalRepository();

        if (userRepo.getUser("admin") == null) {
            String hash = org.mindrot.jbcrypt.BCrypt.hashpw("admin123", org.mindrot.jbcrypt.BCrypt.gensalt());
            userRepo.add(new User("admin", hash, "ADMIN"));
        }

        AuthService authService = new AuthService(userRepo);
        VehicleService vehicleService = new VehicleService(vehicleRepo);
        RentalService rentalService = new RentalService(vehicleRepo, rentalRepo);
        UserService userService = new UserService(userRepo, rentalRepo);

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Vehicle Rental System ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.print("Choose option: ");
        int startOption = scanner.nextInt();
        scanner.nextLine();

        if (startOption == 2) {
            System.out.print("New login: ");
            String newLogin = scanner.nextLine();
            System.out.print("New password: ");
            String newPassword = scanner.nextLine();
            if (authService.register(newLogin, newPassword) != null) {
                System.out.println("Registration successful! Please log in.");
            } else {
                System.out.println("Login already taken. Exiting.");
                scanner.close();
                return;
            }
        }

        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User loggedIn = authService.login(login, password);
        if (loggedIn == null) {
            System.out.println("Invalid credentials. Exiting.");
            scanner.close();
            return;
        }

        System.out.println("Welcome, " + loggedIn.getLogin() + " [" + loggedIn.getRole() + "]");

        if (loggedIn.getRole().equals("ADMIN")) {
            adminMenu(scanner, vehicleService, userService, rentalRepo);
        } else {
            userMenu(scanner, vehicleService, rentalService, loggedIn);
        }

        scanner.close();
    }

    static void adminMenu(Scanner scanner, VehicleService vehicleService,
                          UserService userService, RentalRepository rentalRepo) {
        while (true) {
            System.out.println("\n1. Show all vehicles");
            System.out.println("2. Add vehicle");
            System.out.println("3. Remove vehicle");
            System.out.println("4. Show users");
            System.out.println("5. Show all rentals");
            System.out.println("6. Remove user");
            System.out.println("7. Exit");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> vehicleService.getAllVehicles().forEach(System.out::println);
                case 2 -> {
                    System.out.println("Type (CAR/MOTORCYCLE):"); String type = scanner.nextLine();
                    System.out.println("ID:");                    String id = scanner.nextLine();
                    System.out.println("Brand:");                 String brand = scanner.nextLine();
                    System.out.println("Model:");                 String model = scanner.nextLine();
                    System.out.println("Year:");                  int year = scanner.nextInt();
                    System.out.println("Price:");                 double price = scanner.nextDouble();
                    scanner.nextLine();
                    if (type.equalsIgnoreCase("CAR")) {
                        vehicleService.addVehicle(new Car(id, brand, model, year, price, false));
                    } else if (type.equalsIgnoreCase("MOTORCYCLE")) {
                        System.out.println("Category (A/A1/A2/AM/B):"); String category = scanner.nextLine();
                        vehicleService.addVehicle(new Motorcycle(id, brand, model, year, price, false, category));
                    }
                    System.out.println("Vehicle added.");
                }
                case 3 -> {
                    System.out.println("Enter vehicle ID to remove:");
                    String id = scanner.nextLine();
                    System.out.println(vehicleService.removeVehicle(id) ? "Removed." : "Not found.");
                }
                case 4 -> userService.getAllUsers().forEach(System.out::println);
                case 5 -> rentalRepo.getAllRentals().forEach(System.out::println);
                case 6 -> {
                    System.out.println("Enter login to remove:");
                    String userLogin = scanner.nextLine();
                    System.out.println(userService.removeUser(userLogin) ? "User removed." : "Cannot remove user.");
                }
                case 7 -> { System.out.println("Exiting..."); return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    static void userMenu(Scanner scanner, VehicleService vehicleService,
                         RentalService rentalService, User loggedIn) {
        while (true) {
            System.out.println("\n1. Show available vehicles");
            System.out.println("2. Rent vehicle");
            System.out.println("3. Return vehicle");
            System.out.println("4. Exit");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> vehicleService.getAvailableVehicles().forEach(System.out::println);
                case 2 -> {
                    System.out.println("Enter vehicle ID to rent:");
                    String id = scanner.nextLine();
                    Rental r = rentalService.rent(loggedIn.getLogin(), id);
                    System.out.println(r != null ? "Rented: " + r : "Cannot rent vehicle.");
                }
                case 3 -> {
                    Rental r = rentalService.returnVehicle(loggedIn.getLogin());
                    System.out.println(r != null ? "Returned: " + r : "No active rental.");
                }
                case 4 -> { System.out.println("Exiting..."); return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}
