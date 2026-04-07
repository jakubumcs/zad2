package cwiczenia;

public class TEST {
    public static void main(String[] args) {
        // Use a temporary test CSV so we don't touch the real users.csv
        UserRepository repo = new UserRepository("test_users.csv");
        Authentication auth = new Authentication(repo);

        // 1. Register a test admin (or overwrite if exists)
        String adminPassword = "admin123"; // known password for test
        String hash = Authentication.hashPassword(adminPassword);
        User admin = repo.getUser("admin");
        if (admin != null) {
            admin = new User(admin.getLogin(), hash, admin.getRole(), admin.getRentedVehicleId());
            repo.update(admin);
        } else {
            repo.add(new User("admin", hash, "ADMIN", null));
        }

        // 2. Authenticate as admin
        User loggedIn = auth.authenticate("admin", adminPassword);
        if (loggedIn != null && "ADMIN".equals(loggedIn.getRole())) {
            System.out.println("Successfully logged in as admin!");
        } else {
            System.out.println("Failed to log in as admin.");
        }

        // 3. Optional: clean up test CSV
        new java.io.File("test_users.csv").delete();
    }
}