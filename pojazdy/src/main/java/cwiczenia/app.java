package cwiczenia;

import java.util.List;
import java.util.Scanner;

public class app {

    public static void main(String[] args) {
        VehicleRepositoryImpl vehicleRepo = new VehicleRepositoryImpl();
        UserRepository userRepo = new UserRepository();
        Authentication auth = new Authentication(userRepo);
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

            if (auth.register(newLogin, newPassword)) {
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

        User loggedIn = auth.authenticate(login, password);
        if (loggedIn == null) {
            System.out.println("Invalid credentials. Exiting.");
            scanner.close();
            return;
        }

        System.out.println("Welcome, " + loggedIn.getLogin() + " [" + loggedIn.getRole() + "]");

        if (loggedIn.getRole().equals("ADMIN")) {
            adminMenu(scanner, vehicleRepo, userRepo);
        } else {
            userMenu(scanner, vehicleRepo, userRepo, loggedIn);
        }

        scanner.close();
    }

    static void adminMenu(Scanner scanner, VehicleRepositoryImpl vehicleRepo, UserRepository userRepo) {
        Authentication auth = new Authentication(userRepo); // reuse Authentication
        while (true) {
            System.out.println("\n1. Show vehicles");
            System.out.println("2. Add vehicle");
            System.out.println("3. Remove vehicle");
            System.out.println("4. Show users");
            System.out.println("5. Exit");
            System.out.println("6. Remove user");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> {
                    for (Vehicle v : vehicleRepo.getVehicles()) {
                        System.out.println(v);
                    }
                }
                case 2 -> {
                    System.out.println("Type (CAR/MOTORCYCLE):");
                    String type = scanner.nextLine();
                    System.out.println("ID:");
                    String id = scanner.nextLine();
                    System.out.println("Brand:");
                    String brand = scanner.nextLine();
                    System.out.println("Model:");
                    String model = scanner.nextLine();
                    System.out.println("Year:");
                    int year = scanner.nextInt();
                    System.out.println("Price:");
                    double price = scanner.nextDouble();
                    scanner.nextLine();

                    if (type.equalsIgnoreCase("CAR")) {
                        vehicleRepo.add(new Car(id, brand, model, year, price, false));
                    } else if (type.equalsIgnoreCase("MOTORCYCLE")) {
                        System.out.println("Category (A/A1/A2/AM/B):");
                        String category = scanner.nextLine();
                        vehicleRepo.add(new Motorcycle(id, brand, model, year, price, false, category));
                    }
                    System.out.println("Vehicle added.");
                }
                case 3 -> {
                    System.out.println("Enter vehicle ID to remove:");
                    String id = scanner.nextLine();
                    vehicleRepo.remove(id);
                    System.out.println("Vehicle removed.");
                }
                case 4 -> {
                    List<User> users = userRepo.getUsers();
                    for (User u : users) {
                        System.out.print(u);
                        if (u.getRentedVehicleId() != null) {
                            Vehicle v = vehicleRepo.getVehicle(u.getRentedVehicleId());
                            if (v != null) System.out.print(" -> " + v);
                        }
                        System.out.println();
                    }
                }
                case 5 -> {
                    System.out.println("Exiting...");
                    return;
                }
                case 6 -> {
                    System.out.println("Enter login of user to remove:");
                    String userLogin = scanner.nextLine();

                    if (userLogin.equals("admin")) {
                        System.out.println("Cannot remove the admin account!");
                        break;
                    }

                    if (userRepo.getUser(userLogin) == null) {
                        System.out.println("User not found.");
                        break;
                    }

                    if (auth.removeUser(userLogin)) {
                        System.out.println("User removed successfully.");
                    } else {
                        System.out.println("Cannot remove user (they may have a rented vehicle).");
                    }
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    static void userMenu(Scanner scanner, VehicleRepositoryImpl vehicleRepo, UserRepository userRepo, User loggedIn) {
        while (true) {
            System.out.println("\n1. Show vehicles");
            System.out.println("2. Rent vehicle");
            System.out.println("3. Return vehicle");
            System.out.println("4. My info");
            System.out.println("5. Exit");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> {
                    for (Vehicle v : vehicleRepo.getVehicles()) {
                        System.out.println(v);
                    }
                }
                case 2 -> {
                    if (loggedIn.getRentedVehicleId() != null) {
                        System.out.println("You already have a rented vehicle.");
                        break;
                    }
                    System.out.println("Enter vehicle ID to rent:");
                    String id = scanner.nextLine();
                    if (vehicleRepo.rentVehicle(id)) {
                        loggedIn.setRentedVehicleId(id);
                        userRepo.update(loggedIn);
                        System.out.println("Vehicle rented.");
                    } else {
                        System.out.println("Cannot rent vehicle.");
                    }
                }
                case 3 -> {
                    if (loggedIn.getRentedVehicleId() == null) {
                        System.out.println("You have no rented vehicle.");
                        break;
                    }
                    String id = loggedIn.getRentedVehicleId();
                    if (vehicleRepo.returnVehicle(id)) {
                        loggedIn.setRentedVehicleId(null);
                        userRepo.update(loggedIn);
                        System.out.println("Vehicle returned.");
                    } else {
                        System.out.println("Cannot return vehicle.");
                    }
                }
                case 4 -> {
                    System.out.println(loggedIn);
                    if (loggedIn.getRentedVehicleId() != null) {
                        Vehicle v = vehicleRepo.getVehicle(loggedIn.getRentedVehicleId());
                        if (v != null) System.out.println("Rented vehicle: " + v);
                    }
                }
                case 5 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}