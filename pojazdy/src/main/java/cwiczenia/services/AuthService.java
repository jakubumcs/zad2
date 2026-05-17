package cwiczenia.services;

import cwiczenia.models.User;
import cwiczenia.repositories.IUserRepository;
import cwiczenia.services.interfaces.AuthServiceInterface;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService implements AuthServiceInterface {

    private final IUserRepository userRepository;

    public AuthService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> login(String login, String password) {
        User user = userRepository.getUser(login);
        if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public boolean register(String login, String password) {
        if (userRepository.getUser(login) != null) return false;
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(login, hash, "USER");
        userRepository.add(user);
        return true;
    }
}
