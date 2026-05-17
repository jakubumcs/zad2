package cwiczenia.services.interfaces;

import cwiczenia.models.User;

import java.util.Optional;

public interface AuthServiceInterface {
    boolean register(String login, String rawPassword);
    Optional<User> login(String login, String rawPassword);
}
