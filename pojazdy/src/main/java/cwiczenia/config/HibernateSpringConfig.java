package cwiczenia.config;

import cwiczenia.models.Rental;
import cwiczenia.models.User;
import cwiczenia.models.Vehicle;
import cwiczenia.repositories.hibernate.session.HibernateSessionManager;
import cwiczenia.repositories.hibernate.session.HibernateSessionManagerImpl;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("jpa")
public class HibernateSpringConfig {

    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        Map<String, Object> settings = new HashMap<>();
        // Reuse the Spring-managed (HikariCP pooled) DataSource instead of having
        // Hibernate open its own raw, unpooled JDBC connections. This avoids a second,
        // competing connection source hitting Neon's connection limit at startup.
        settings.put(Environment.DATASOURCE, dataSource);
        settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        settings.put(Environment.HBM2DDL_AUTO, "update");
        settings.put(Environment.SHOW_SQL, "false");
        settings.put(Environment.FORMAT_SQL, "true");

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

    @Bean
    public HibernateSessionManager hibernateSessionManager(SessionFactory sessionFactory) {
        return new HibernateSessionManagerImpl(sessionFactory);
    }
}