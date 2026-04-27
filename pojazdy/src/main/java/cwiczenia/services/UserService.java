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

    public List<User> findAllUsers() {
        return userRepository.getUsers();
    }

    public User getUser(String login) {
        return userRepository.getUser(login);
    }

    public User findById(String id) {
        return userRepository.getUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika o ID: " + id));
    }

    public boolean removeUser(String login) {
        if ("admin".equals(login)) return false;
        User user = userRepository.getUser(login);
        if (user == null) return false;
        if (rentalRepository.getActiveRentalByUser(user.getId()) != null) return false;
        userRepository.remove(login);
        return true;
    }

    public void deleteUser(String userIdToDelete, String requesterId) {
        if (userIdToDelete.equals(requesterId))
            throw new IllegalArgumentException("Nie możesz usunąć samego siebie.");

        User toDelete = findById(userIdToDelete);

        if ("ADMIN".equals(toDelete.getRole()))
            throw new IllegalArgumentException("Nie można usunąć konta administratora.");

        if (rentalRepository.getActiveRentalByUser(userIdToDelete) != null)
            throw new IllegalStateException("Nie można usunąć użytkownika z aktywnym wypożyczeniem.");

        userRepository.remove(toDelete.getLogin());
    }
}
