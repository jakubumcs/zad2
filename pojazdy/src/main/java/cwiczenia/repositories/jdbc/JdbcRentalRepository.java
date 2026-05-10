package cwiczenia.repositories.jdbc;

import cwiczenia.models.Rental;
import cwiczenia.repositories.IRentalRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcRentalRepository implements IRentalRepository {

    private final String url;

    public JdbcRentalRepository(String url) {
        this.url = requireUrl(url);
    }

    @Override
    public void add(Rental rental) {
        String sql = """
                INSERT INTO rental (id, vehicle_id, user_id, rent_date_time, return_date_time)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE
                SET vehicle_id = EXCLUDED.vehicle_id,
                    user_id = EXCLUDED.user_id,
                    rent_date_time = EXCLUDED.rent_date_time,
                    return_date_time = EXCLUDED.return_date_time
                """;
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillRentalStatement(statement, rental);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save rental: " + rental.getId(), e);
        }
    }

    @Override
    public void update(Rental rental) {
        add(rental);
    }

    @Override
    public Rental getActiveRentalByUser(String userId) {
        String sql = """
                SELECT id, vehicle_id, user_id, rent_date_time, return_date_time
                FROM rental
                WHERE user_id = ? AND return_date_time IS NULL
                """;
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRental(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch active rental for user: " + userId, e);
        }
    }

    @Override
    public Rental getActiveRentalByVehicle(String vehicleId) {
        String sql = """
                SELECT id, vehicle_id, user_id, rent_date_time, return_date_time
                FROM rental
                WHERE vehicle_id = ? AND return_date_time IS NULL
                """;
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, vehicleId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRental(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch active rental for vehicle: " + vehicleId, e);
        }
    }

    @Override
    public List<Rental> getAllRentals() {
        String sql = "SELECT id, vehicle_id, user_id, rent_date_time, return_date_time FROM rental";
        List<Rental> rentals = new ArrayList<>();
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                rentals.add(mapRental(resultSet));
            }
            return rentals;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch rentals", e);
        }
    }

    @Override
    public List<Rental> getRentalsByUser(String userId) {
        String sql = """
                SELECT id, vehicle_id, user_id, rent_date_time, return_date_time
                FROM rental
                WHERE user_id = ?
                """;
        List<Rental> rentals = new ArrayList<>();
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rentals.add(mapRental(resultSet));
                }
            }
            return rentals;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch rentals for user: " + userId, e);
        }
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    private void fillRentalStatement(PreparedStatement statement, Rental rental) throws SQLException {
        statement.setString(1, rental.getId());
        statement.setString(2, rental.getVehicleId());
        statement.setString(3, rental.getUserId());
        statement.setString(4, stringify(rental.getRentDateTime()));
        if (rental.getReturnDateTime() == null) {
            statement.setNull(5, Types.VARCHAR);
        } else {
            statement.setString(5, stringify(rental.getReturnDateTime()));
        }
    }

    private Rental mapRental(ResultSet resultSet) throws SQLException {
        return new Rental(
                resultSet.getString("id"),
                resultSet.getString("user_id"),
                resultSet.getString("vehicle_id"),
                parse(resultSet.getString("rent_date_time")),
                parse(resultSet.getString("return_date_time"))
        );
    }

    private String stringify(LocalDateTime value) {
        return value == null ? null : value.toString();
    }

    private LocalDateTime parse(String value) {
        return value == null ? null : LocalDateTime.parse(value);
    }

    private static String requireUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("DATABASE_URL is required in jdbc mode.");
        }
        return url;
    }
}
