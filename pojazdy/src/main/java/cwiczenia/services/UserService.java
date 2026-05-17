package cwiczenia.services;

import cwiczenia.models.User;
import cwiczenia.repositories.IRentalRepository;
import cwiczenia.repositories.IUserRepository;
import cwiczenia.services.interfaces.UserServiceInterface;

import java.util.List;

public class UserService implements UserServiceInterface {

    private final IUserRepository userRepository;
    private final IRentalRepository rentalRepository;

    public UserService(IUserRepository userRepository, IRentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.getUsers();
    }

    @Override
    public User findById(String id) {
        return userRepository.getUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika o ID: " + id));
    }

    public User findByIdOrLogin(String identifier) {
        return userRepository.getUsers().stream()
                .filter(u -> u.getId().equals(identifier) || u.getLogin().equals(identifier))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nie znaleziono użytkownika o ID lub loginie: " + identifier));
    }

    public void deleteUser(String userIdentifierToDelete, String requesterId) {
        User toDelete = findByIdOrLogin(userIdentifierToDelete);

        if (toDelete.getId().equals(requesterId))
            throw new IllegalArgumentException("Nie możesz usunąć samego siebie.");

        if ("ADMIN".equals(toDelete.getRole()))
            throw new IllegalArgumentException("Nie można usunąć konta administratora.");

        if (rentalRepository.getActiveRentalByUser(toDelete.getId()) != null)
            throw new IllegalStateException("Nie można usunąć użytkownika z aktywnym wypożyczeniem.");

        userRepository.remove(toDelete.getLogin());
    }
}
