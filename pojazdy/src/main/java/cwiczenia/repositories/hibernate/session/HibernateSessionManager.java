package cwiczenia.repositories.hibernate.session;

import org.hibernate.Session;

import java.util.function.Consumer;
import java.util.function.Function;

public interface HibernateSessionManager {
    <T> T execute(Function<Session, T> action);
    <T> T executeInTransaction(Function<Session, T> action);
    void executeInTransaction(Consumer<Session> action);
}
