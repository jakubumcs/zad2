package cwiczenia;

import java.util.List;

public interface IUserRepository {
    User getUser(String login);
    List<User> getUsers();
    void save();
    void load();
    void update(User user);
    void add(User user);
}