package cwiczenia;

import java.util.List;

public interface IUserRepository {
    User getUser(String login);
    List<User> getUsers();
    void add(User user);
    void update(User user);
    void remove(String login);
}