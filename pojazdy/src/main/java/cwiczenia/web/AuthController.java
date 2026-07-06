package cwiczenia.web;

import cwiczenia.models.User;
import cwiczenia.security.JwtTokenProvider;
import cwiczenia.services.interfaces.AuthServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthServiceInterface authService;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthServiceInterface authService, JwtTokenProvider tokenProvider) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> body) {
        String login = body.get("login");
        String password = body.get("password");
        String address = body.get("address");
        if (login == null || password == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Login i hasło są wymagane."));
        boolean success = authService.register(login, password, address);
        if (!success)
            return ResponseEntity.badRequest().body(Map.of("error", "Użytkownik już istnieje."));
        return ResponseEntity.ok(Map.of("message", "Zarejestrowano pomyślnie."));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String login = body.get("login");
        String password = body.get("password");
        Optional<User> user = authService.login(login, password);
        if (user.isEmpty())
            return ResponseEntity.status(401).body(Map.of("error", "Nieprawidłowy login lub hasło."));
        String token = tokenProvider.generateToken(user.get().getId(), user.get().getRole());
        return ResponseEntity.ok(Map.of("token", token));
    }
}