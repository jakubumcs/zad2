package cwiczenia;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IUserRepository {

    private final List<User> users = new ArrayList<>();
    private final String FILE_NAME;

    public UserRepository() { this("users.csv"); }

    public UserRepository(String fileName) {
        this.FILE_NAME = fileName;
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
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (User u : users)
                writer.println(u.getLogin() + ";" + u.getPassword() + ";" + u.getRole());
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    private void load() {
        users.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length < 3) continue;
                users.add(new User(parts[0], parts[1], parts[2]));
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }
}