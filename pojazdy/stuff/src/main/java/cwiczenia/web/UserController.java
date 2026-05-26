package cwiczenia.web;

import cwiczenia.models.User;
import cwiczenia.services.interfaces.UserServiceInterface;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceInterface userService;

    public UserController(UserServiceInterface userService) {
        this.userService = userService;
    }

    // GET /api/users → lista wszystkich użytkowników
    @GetMapping
    public List<User> list() {
        return userService.findAllUsers();
    }

    // GET /api/users/{id} → jeden użytkownik po ID
    @GetMapping("/{id}")
    public User get(@PathVariable String id) {
        return userService.findById(id);
    }
}
