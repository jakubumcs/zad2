package cwiczenia.repositories.hibernate;

import cwiczenia.models.User;
import cwiczenia.repositories.IUserRepository;
import cwiczenia.repositories.hibernate.session.HibernateSessionManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.hibernate.Session;

import java.util.List;

@Repository
@Profile("jpa")
public class HibernateUserRepository implements IUserRepository {

    private final HibernateSessionManager sessionManager;

    public HibernateUserRepository(HibernateSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public User getUser(String login) {
        return sessionManager.execute(session -> findByLogin(session, login));
    }

    @Override
    public List<User> getUsers() {
        return sessionManager.execute(session ->
                session.createQuery("from User order by login", User.class).getResultList());
    }

    @Override
    public void add(User user) {
        sessionManager.executeInTransaction(session -> session.merge(user));
    }

    @Override
    public void update(User user) { add(user); }

    @Override
    public void remove(String login) {
        sessionManager.executeInTransaction(session -> {
            User user = findByLogin(session, login);
            if (user != null) session.remove(user);
        });
    }

    private User findByLogin(Session session, String login) {
        return session.createQuery("from User where login = :login", User.class)
                .setParameter("login", login)
                .uniqueResult();
    }
}
