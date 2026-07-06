package cwiczenia.web;

import cwiczenia.models.User;
import cwiczenia.services.interfaces.UserServiceInterface;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
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

    @GetMapping
    public List<User> list() {
        return userService.findAllUsers();
    }

    @GetMapping("/me")
    public User me(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        return userService.findById(userId);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable String id, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isSelf = authentication.getPrincipal().equals(id);
        if (!isAdmin && !isSelf) {
            throw new AccessDeniedException("Nie masz dostępu do danych innego użytkownika.");
        }
        return userService.findById(id);
    }
}
