package cwiczenia.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

// @ControllerAdvice to globalny handler wyjątków dla wszystkich kontrolerów.
// Zamiast obsługiwać wyjątki w każdym kontrolerze osobno, definiujemy je raz tutaj.
// Serwis rzuca wyjątki Javy (IllegalArgumentException, IllegalStateException),
// a ten handler zamienia je na czytelne odpowiedzi HTTP z kodem i komunikatem JSON.
@ControllerAdvice
public class GlobalExceptionHandler {

    // IllegalArgumentException → 400 Bad Request
    // Używane gdy dane wejściowe są niepoprawne:
    // brak kategorii, błędny atrybut, nieznany pojazd/użytkownik
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }

    // IllegalStateException → 409 Conflict
    // Używane gdy żądanie jest logicznie sprzeczne ze stanem systemu:
    // pojazd już wypożyczony, użytkownik ma aktywne wypożyczenie
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> conflict(IllegalStateException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));
    }
}
