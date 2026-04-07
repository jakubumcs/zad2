package cwiczenia;

import org.apache.commons.codec.digest.DigestUtils;

public class Authentication {

    private final IUserRepository userRepository;

    public Authentication(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String login, String password) {
        String hash = hashPassword(password);
        User user = userRepository.getUser(login);
        if (user != null && user.getPassword().equals(hash)) {
            return user;
        }
        return null;
    }

    public static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }
}