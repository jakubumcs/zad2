package cwiczenia.services;

import cwiczenia.models.User;
import cwiczenia.repositories.IUserRepository;
import cwiczenia.services.interfaces.AuthServiceInterface;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements AuthServiceInterface {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> login(String login, String password) {
        User user = userRepository.getUser(login);
        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public boolean register(String login, String password, String address) {
        if (userRepository.getUser(login) != null) return false;
        String hash = passwordEncoder.encode(password);
        User user = new User(login, hash, "USER", address);
        userRepository.add(user);
        return true;
    }
}