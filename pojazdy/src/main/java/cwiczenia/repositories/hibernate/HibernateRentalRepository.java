package cwiczenia.repositories.hibernate;

import cwiczenia.models.Rental;
import cwiczenia.repositories.IRentalRepository;
import cwiczenia.repositories.hibernate.session.HibernateSessionManager;
import org.hibernate.Session;

import java.util.List;

public class HibernateRentalRepository implements IRentalRepository {

    private final HibernateSessionManager sessionManager;

    public HibernateRentalRepository(HibernateSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void add(Rental rental) {
        sessionManager.executeInTransaction(session -> session.merge(rental));
    }

    @Override
    public void update(Rental rental) {
        add(rental);
    }

    @Override
    public Rental getActiveRentalByUser(String userId) {
        return sessionManager.execute(session -> findActiveRentalByUser(session, userId));
    }

    @Override
    public Rental getActiveRentalByVehicle(String vehicleId) {
        return sessionManager.execute(session -> findActiveRentalByVehicle(session, vehicleId));
    }

    @Override
    public List<Rental> getAllRentals() {
        return sessionManager.execute(session ->
                session.createQuery(
                                "select r from Rental r join fetch r.user join fetch r.vehicle order by r.rentDateTime desc",
                                Rental.class)
                        .getResultList());
    }

    @Override
    public List<Rental> getRentalsByUser(String userId) {
        return sessionManager.execute(session ->
                session.createQuery(
                                "select r from Rental r join fetch r.user join fetch r.vehicle where r.user.id = :userId order by r.rentDateTime desc",
                                Rental.class)
                        .setParameter("userId", userId)
                        .getResultList());
    }

    private Rental findActiveRentalByUser(Session session, String userId) {
        return session.createQuery(
                        "select r from Rental r join fetch r.user join fetch r.vehicle where r.user.id = :userId and r.returnDateTime is null",
                        Rental.class)
                .setParameter("userId", userId)
                .uniqueResult();
    }

    private Rental findActiveRentalByVehicle(Session session, String vehicleId) {
        return session.createQuery(
                        "select r from Rental r join fetch r.user join fetch r.vehicle where r.vehicle.id = :vehicleId and r.returnDateTime is null",
                        Rental.class)
                .setParameter("vehicleId", vehicleId)
                .uniqueResult();
    }
}
