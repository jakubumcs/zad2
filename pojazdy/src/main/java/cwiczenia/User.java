package cwiczenia;

public class User {
    private String login;
    private String password; // hash SHA-256
    private String role;
    private String rentedVehicleId;

    public User(String login, String password, String role, String rentedVehicleId) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicleId = rentedVehicleId;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getRentedVehicleId() {
        return rentedVehicleId;
    }

    public void setRentedVehicleId(String rentedVehicleId) {
        this.rentedVehicleId = rentedVehicleId;
    }


    public User copy() {
        return new User(login, password, role, rentedVehicleId);
    }

    @Override
    public String toString() {
        return "login=" + login + " role=" + role + " rentedVehicleId=" + rentedVehicleId;
    }
}