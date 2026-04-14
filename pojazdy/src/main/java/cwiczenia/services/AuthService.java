package cwiczenia.services;

import cwiczenia.models.User;
import cwiczenia.repositories.IUserRepository;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    private final IUserRepository userRepository;

    public AuthService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String login, String password) {
        User user = userRepository.getUser(login);
        if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    public User register(String login, String password) {
        if (userRepository.getUser(login) != null) return null;
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(login, hash, "USER");
        userRepository.add(user);
        return user;
    }
}