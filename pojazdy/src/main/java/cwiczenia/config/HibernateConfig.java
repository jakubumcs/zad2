package cwiczenia.config;

import cwiczenia.models.Rental;
import cwiczenia.models.User;
import cwiczenia.models.Vehicle;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.HashMap;
import java.util.Map;

public final class HibernateConfig {

    private static SessionFactory sessionFactory;

    private HibernateConfig() {
    }

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }
        return sessionFactory;
    }

    public static synchronized void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }

    private static SessionFactory buildSessionFactory() {
        String dbUrl = System.getenv("DATABASE_URL");
        if (dbUrl == null || dbUrl.isBlank()) {
            throw new IllegalArgumentException("DATABASE_URL is required in hibernate mode.");
        }

        Map<String, Object> settings = new HashMap<>();
        settings.put("hibernate.connection.url", normalizeJdbcUrl(dbUrl));
        settings.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        settings.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        settings.put("hibernate.hbm2ddl.auto", "update");
        settings.put("hibernate.show_sql", "false");
        settings.put("hibernate.format_sql", "true");

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();

        try {
            return new MetadataSources(registry)
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Vehicle.class)
                    .addAnnotatedClass(Rental.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw new IllegalStateException("Failed to initialize Hibernate SessionFactory.", e);
        }
    }

    private static String normalizeJdbcUrl(String dbUrl) {
        if (dbUrl.startsWith("jdbc:")) {
            return dbUrl;
        }
        if (dbUrl.startsWith("postgresql://") || dbUrl.startsWith("postgres://")) {
            return "jdbc:" + dbUrl;
        }
        return dbUrl;
    }
}
