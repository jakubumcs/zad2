package cwiczenia.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String role;

    @Column(name = "address")
    private String address;

    public User() {}

    public User(String login, String passwordHash, String role) {
        this(login, passwordHash, role, null);
    }

    public User(String login, String passwordHash, String role, String address) {
        this.id = UUID.randomUUID().toString();
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
        this.address = address;
    }

    public User(String id, String login, String passwordHash, String role, String address) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
        this.address = address;
    }

    public String getId() { return id; }
    public String getLogin() { return login; }
    public String getPasswordHash() { return passwordHash; }
    @JsonIgnore
    public String getPassword() { return passwordHash; }
    public String getRole() { return role; }
    public String getAddress() { return address; }

    public void setId(String id) { this.id = id; }
    public void setLogin(String login) { this.login = login; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(String role) { this.role = role; }
    public void setAddress(String address) { this.address = address; }

    public User copy() {
        return new User(id, login, passwordHash, role, address);
    }

    @Override
    public String toString() {
        return "id=" + id + " login=" + login + " role=" + role + " address=" + address;
    }
}
