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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

// @Configuration oznacza klasę jako źródło beanów konfiguracyjnych Springa.
// Metody z @Bean są wywoływane przez Springa i ich wyniki trafiają do kontenera jako beany.
@Configuration
// @Profile("jpa") – cała ta konfiguracja aktywna tylko przy APP_PROFILE=jpa
@Profile("jpa")
public class HibernateSpringConfig {

    // @Value wstrzykuje wartość zmiennej środowiskowej DB_URL z application-jpa.yml
    @Value("${spring.datasource.url}")
    private String dbUrl;

    // @Bean mówi Springowi: "ta metoda zwraca obiekt, który ma być beanem w kontenerze".
    // Spring wywoła tę metodę raz i zachowa wynik jako singleton.
    @Bean
    public SessionFactory sessionFactory() {
        String normalizedUrl = normalizeJdbcUrl(dbUrl);

        Map<String, Object> settings = new HashMap<>();
        settings.put("hibernate.connection.url", normalizedUrl);
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

    // HibernateSessionManager jest zależnością repozytoriów JPA.
    // Spring wstrzyknie sessionFactory() do tej metody automatycznie.
    @Bean
    public HibernateSessionManager hibernateSessionManager(SessionFactory sessionFactory) {
        return new HibernateSessionManagerImpl(sessionFactory);
    }

    private String normalizeJdbcUrl(String url) {
        if (url.startsWith("jdbc:")) return url;
        if (url.startsWith("postgresql://") || url.startsWith("postgres://")) return "jdbc:" + url;
        return url;
    }
}
