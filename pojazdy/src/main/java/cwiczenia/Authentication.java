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
    public boolean register(String login, String password) {


        if (userRepository.getUser(login) != null) {
            return false;
        }

        String hash = hashPassword(password);

        User user = new User(login, hash, "USER", null);
        userRepository.add(user);

        return true;
    }
    public boolean removeUser(String login) {

        User user = userRepository.getUser(login);

        if (user == null) {
            return false;
        }

        if (user.getRentedVehicleId() != null) {
            return false;
        }

        userRepository.remove(login);
        return true;
    }

    public static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }
}