package cwiczenia.services.interfaces;

import cwiczenia.models.User;

import java.util.List;

public interface UserServiceInterface {
    List<User> findAllUsers();
    User findById(String id);
    void deleteUser(String id, String loggedUserId);
}
