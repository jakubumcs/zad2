package cwiczenia.repositories.hibernate;

import cwiczenia.models.Vehicle;
import cwiczenia.repositories.IVehicleRepository;
import cwiczenia.repositories.hibernate.session.HibernateSessionManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;

// @Profile("jpa") – ten bean aktywny tylko przy APP_PROFILE=jpa
@Repository
@Profile("jpa")
public class HibernateVehicleRepository implements IVehicleRepository {

    private final HibernateSessionManager sessionManager;

    public HibernateVehicleRepository(HibernateSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void add(Vehicle vehicle) {
        sessionManager.executeInTransaction(session -> session.merge(vehicle));
    }

    @Override
    public void remove(String id) {
        sessionManager.executeInTransaction(session -> {
            Vehicle vehicle = session.find(Vehicle.class, id);
            if (vehicle != null) session.remove(vehicle);
        });
    }

    @Override
    public Vehicle getVehicle(String id) {
        return sessionManager.execute(session -> session.find(Vehicle.class, id));
    }

    @Override
    public List<Vehicle> getVehicles() {
        return sessionManager.execute(session ->
                session.createQuery("from Vehicle order by category, brand, model", Vehicle.class).getResultList());
    }

    @Override
    public void update(Vehicle vehicle) { add(vehicle); }
}
