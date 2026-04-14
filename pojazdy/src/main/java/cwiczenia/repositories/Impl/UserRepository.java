package cwiczenia.repositories.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cwiczenia.models.User;
import cwiczenia.repositories.IUserRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserRepository implements IUserRepository {

    private final List<User> users = new ArrayList<>();
    private final String FILE_NAME;
    private final ObjectMapper mapper;

    public UserRepository() { this("users.json"); }

    public UserRepository(String fileName) {
        this.FILE_NAME = fileName;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
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

    private void save() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_NAME), users);
        } catch (Exception e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    private void load() {
        users.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try {
            User[] loaded = mapper.readValue(file, User[].class);
            users.addAll(Arrays.asList(loaded));
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }
}