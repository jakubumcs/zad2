package cwiczenia;

import cwiczenia.models.Rental;
import cwiczenia.models.User;
import cwiczenia.models.Vehicle;
import cwiczenia.repositories.Impl.RentalRepository;
import cwiczenia.repositories.Impl.UserRepository;
import cwiczenia.repositories.Impl.VehicleRepositoryImpl;
import cwiczenia.repositories.IRentalRepository;
import cwiczenia.repositories.IUserRepository;
import cwiczenia.repositories.IVehicleRepository;
import cwiczenia.repositories.jdbc.JdbcRentalRepository;
import cwiczenia.repositories.jdbc.JdbcUserRepository;
import cwiczenia.repositories.jdbc.JdbcVehicleRepository;

import java.util.List;

public class JsonToJdbcImport {

    public static void main(String[] args) {
        String dbUrl = System.getenv("DATABASE_URL");
        if (dbUrl == null || dbUrl.isBlank()) {
            throw new IllegalArgumentException("DATABASE_URL is required.");
        }

        IUserRepository jsonUserRepository = new UserRepository();
        IVehicleRepository jsonVehicleRepository = new VehicleRepositoryImpl();
        IRentalRepository jsonRentalRepository = new RentalRepository();

        IUserRepository jdbcUserRepository = new JdbcUserRepository(dbUrl);
        IVehicleRepository jdbcVehicleRepository = new JdbcVehicleRepository(dbUrl);
        IRentalRepository jdbcRentalRepository = new JdbcRentalRepository(dbUrl);

        List<User> users = jsonUserRepository.getUsers();
        for (User user : users) {
            jdbcUserRepository.add(user);
        }

        List<Vehicle> vehicles = jsonVehicleRepository.getVehicles();
        for (Vehicle vehicle : vehicles) {
            jdbcVehicleRepository.add(vehicle);
        }

        List<Rental> rentals = jsonRentalRepository.getAllRentals();
        int importedRentals = 0;
        for (Rental rental : rentals) {
            try {
                jdbcRentalRepository.add(rental);
                importedRentals++;
            } catch (Exception e) {
                System.out.println("Pominięto rental " + rental.getId() + ": " + e.getMessage());
            }
        }

        System.out.println("Import completed.");
        System.out.println("Users imported: " + users.size());
        System.out.println("Vehicles imported: " + vehicles.size());
        System.out.println("Rentals imported: " + importedRentals + "/" + rentals.size());
    }
}
