package cwiczenia;

import cwiczenia.models.User;
import cwiczenia.repositories.Impl.VehicleCategoryConfigRepository;
import cwiczenia.repositories.IRentalRepository;
import cwiczenia.repositories.IUserRepository;
import cwiczenia.repositories.IVehicleRepository;
import cwiczenia.repositories.RepositoryFactory;
import cwiczenia.services.AuthService;
import cwiczenia.services.RentalService;
import cwiczenia.services.UserService;
import cwiczenia.services.VehicleCategoryConfigService;
import cwiczenia.services.VehicleService;
import cwiczenia.services.VehicleValidator;

public class app {

    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0] : "json";
        String dbUrl = System.getenv("DATABASE_URL");

        IUserRepository userRepo = RepositoryFactory.createUserRepository(mode, dbUrl);
        IVehicleRepository vehicleRepo = RepositoryFactory.createVehicleRepository(mode, dbUrl);
        IRentalRepository rentalRepo = RepositoryFactory.createRentalRepository(mode, dbUrl);
        VehicleCategoryConfigRepository categoryConfigRepo = new VehicleCategoryConfigRepository();

        if (userRepo.getUser("admin") == null) {
            String hash = org.mindrot.jbcrypt.BCrypt.hashpw(
                    "admin123", org.mindrot.jbcrypt.BCrypt.gensalt());
            userRepo.add(new User("admin", hash, "ADMIN"));
            System.out.println("Utworzono domyślne konto admina (login: admin, hasło: admin123)");
        }

        AuthService authService = new AuthService(userRepo);
        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(categoryConfigRepo);
        VehicleValidator vehicleValidator = new VehicleValidator(configService);
        VehicleService vehicleService = new VehicleService(vehicleRepo, rentalRepo, vehicleValidator);
        RentalService rentalService = new RentalService(vehicleRepo, rentalRepo);
        UserService userService = new UserService(userRepo, rentalRepo);

        UI ui = new UI(authService, vehicleService, rentalService, userService, configService);
        ui.start();
    }
}
