package cwiczenia.repositories.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cwiczenia.db.JsonFileStorage;
import cwiczenia.models.User;
import cwiczenia.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("json")
public class UserRepository implements IUserRepository {

    private final List<User> users = new ArrayList<>();
    private final JsonFileStorage<User> storage;

    public UserRepository(@Value("${carrent.json.users-file}") String fileName) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.storage = new JsonFileStorage<>(mapper, fileName, User.class);
        load();
    }

    @Override
    public User getUser(String login) {
        for (User u : users)
            if (u.getLogin().equals(login)) return u.copy();
        return null;
    }

    @Override
    public List<User> getUsers() {
        List<User> copy = new ArrayList<>();
        for (User u : users) copy.add(u.copy());
        return copy;
    }

    @Override
    public void add(User user) {
        users.add(user.copy());
        save();
    }

    @Override
    public void update(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getLogin().equals(user.getLogin())) {
                users.set(i, user.copy());
                save();
                return;
            }
        }
    }

    @Override
    public void remove(String login) {
        if (users.removeIf(u -> u.getLogin().equals(login))) save();
    }

    private void save() { storage.save(users); }
    private void load() { users.clear(); users.addAll(storage.load()); }
}
