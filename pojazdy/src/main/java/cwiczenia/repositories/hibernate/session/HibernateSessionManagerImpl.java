package cwiczenia.repositories.hibernate.session;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.function.Consumer;
import java.util.function.Function;

public class HibernateSessionManagerImpl implements HibernateSessionManager {

    private final SessionFactory sessionFactory;

    public HibernateSessionManagerImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public <T> T execute(Function<Session, T> action) {
        try (Session session = sessionFactory.openSession()) {
            return action.apply(session);
        }
    }

    @Override
    public <T> T executeInTransaction(Function<Session, T> action) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                T result = action.apply(session);
                transaction.commit();
                return result;
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    @Override
    public void executeInTransaction(Consumer<Session> action) {
        executeInTransaction(session -> {
            action.accept(session);
            return null;
        });
    }
}
