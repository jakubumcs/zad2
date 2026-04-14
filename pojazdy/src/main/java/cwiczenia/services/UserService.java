package cwiczenia.services;

import cwiczenia.models.User;
import cwiczenia.repositories.IRentalRepository;
import cwiczenia.repositories.IUserRepository;

import java.util.List;

public class UserService {

    private final IUserRepository userRepository;
    private final IRentalRepository rentalRepository;

    public UserService(IUserRepository userRepository, IRentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.getUsers();
    }

    public boolean removeUser(String login) {
        if (login.equals("admin")) return false;
        User user = userRepository.getUser(login);
        if (user == null) return false;
        if (rentalRepository.getActiveRentalByUser(user.getId()) != null) return false;
        userRepository.remove(login);
        return true;
    }

    public User getUser(String login) {
        return userRepository.getUser(login);
    }
}