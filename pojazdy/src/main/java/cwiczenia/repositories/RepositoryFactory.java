package cwiczenia.repositories;

import cwiczenia.repositories.Impl.RentalRepository;
import cwiczenia.repositories.Impl.UserRepository;
import cwiczenia.repositories.Impl.VehicleRepositoryImpl;
import cwiczenia.repositories.jdbc.JdbcRentalRepository;
import cwiczenia.repositories.jdbc.JdbcUserRepository;
import cwiczenia.repositories.jdbc.JdbcVehicleRepository;

public final class RepositoryFactory {

    private RepositoryFactory() {
    }

    public static IUserRepository createUserRepository(String mode, String dbUrl) {
        if (isJdbcMode(mode)) {
            return new JdbcUserRepository(dbUrl);
        }
        return new UserRepository();
    }

    public static IVehicleRepository createVehicleRepository(String mode, String dbUrl) {
        if (isJdbcMode(mode)) {
            return new JdbcVehicleRepository(dbUrl);
        }
        return new VehicleRepositoryImpl();
    }

    public static IRentalRepository createRentalRepository(String mode, String dbUrl) {
        if (isJdbcMode(mode)) {
            return new JdbcRentalRepository(dbUrl);
        }
        return new RentalRepository();
    }

    private static boolean isJdbcMode(String mode) {
        return "jdbc".equalsIgnoreCase(mode);
    }
}
