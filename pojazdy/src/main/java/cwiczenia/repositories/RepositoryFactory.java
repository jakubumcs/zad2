package cwiczenia.repositories;

import cwiczenia.config.HibernateConfig;
import cwiczenia.repositories.Impl.RentalRepository;
import cwiczenia.repositories.Impl.UserRepository;
import cwiczenia.repositories.Impl.VehicleRepositoryImpl;
import cwiczenia.repositories.hibernate.HibernateRentalRepository;
import cwiczenia.repositories.hibernate.HibernateUserRepository;
import cwiczenia.repositories.hibernate.HibernateVehicleRepository;
import cwiczenia.repositories.hibernate.session.HibernateSessionManager;
import cwiczenia.repositories.hibernate.session.HibernateSessionManagerImpl;
import cwiczenia.repositories.jdbc.JdbcRentalRepository;
import cwiczenia.repositories.jdbc.JdbcUserRepository;
import cwiczenia.repositories.jdbc.JdbcVehicleRepository;

public final class RepositoryFactory {

    private static HibernateSessionManager hibernateSessionManager;

    private RepositoryFactory() {
    }

    public static IUserRepository createUserRepository(String mode, String dbUrl) {
        if (isHibernateMode(mode)) {
            return new HibernateUserRepository(getHibernateSessionManager());
        }
        if (isJdbcMode(mode)) {
            return new JdbcUserRepository(dbUrl);
        }
        return new UserRepository();
    }

    public static IVehicleRepository createVehicleRepository(String mode, String dbUrl) {
        if (isHibernateMode(mode)) {
            return new HibernateVehicleRepository(getHibernateSessionManager());
        }
        if (isJdbcMode(mode)) {
            return new JdbcVehicleRepository(dbUrl);
        }
        return new VehicleRepositoryImpl();
    }

    public static IRentalRepository createRentalRepository(String mode, String dbUrl) {
        if (isHibernateMode(mode)) {
            return new HibernateRentalRepository(getHibernateSessionManager());
        }
        if (isJdbcMode(mode)) {
            return new JdbcRentalRepository(dbUrl);
        }
        return new RentalRepository();
    }

    private static boolean isJdbcMode(String mode) {
        return "jdbc".equalsIgnoreCase(mode);
    }

    private static boolean isHibernateMode(String mode) {
        return "hibernate".equalsIgnoreCase(mode);
    }

    private static HibernateSessionManager getHibernateSessionManager() {
        if (hibernateSessionManager == null) {
            hibernateSessionManager = new HibernateSessionManagerImpl(HibernateConfig.getSessionFactory());
        }
        return hibernateSessionManager;
    }
}
