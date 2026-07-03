package cwiczenia.repositories.jdbc;

import cwiczenia.models.Rental;
import cwiczenia.repositories.IRentalRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("jdbc")
public class JdbcRentalRepository implements IRentalRepository {

    private final DataSource dataSource;

    public JdbcRentalRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void add(Rental rental) {
        String sql = """
                INSERT INTO rental (id, vehicle_id, user_id, rentdatetime, returndatetime)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE
                SET vehicle_id = EXCLUDED.vehicle_id,
                    user_id = EXCLUDED.user_id,
                    rentdatetime = EXCLUDED.rentdatetime,
                    returndatetime = EXCLUDED.returndatetime
                """;
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            fillRentalStatement(statement, rental);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save rental: " + rental.getId(), e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void update(Rental rental) { add(rental); }

    @Override
    public void removeAll() {
        String sql = "DELETE FROM rental";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to remove rentals", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public Rental getActiveRentalByUser(String userId) {
        String sql = """
                SELECT id, vehicle_id, user_id, rentdatetime, returndatetime
                FROM rental WHERE user_id = ? AND returndatetime IS NULL
                """;
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) return mapRental(resultSet);
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch active rental for user: " + userId, e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public Rental getActiveRentalByVehicle(String vehicleId) {
        String sql = """
                SELECT id, vehicle_id, user_id, rentdatetime, returndatetime
                FROM rental WHERE vehicle_id = ? AND returndatetime IS NULL
                """;
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, vehicleId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) return mapRental(resultSet);
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch active rental for vehicle: " + vehicleId, e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public List<Rental> getAllRentals() {
        String sql = "SELECT id, vehicle_id, user_id, rentdatetime, returndatetime FROM rental";
        List<Rental> rentals = new ArrayList<>();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) rentals.add(mapRental(resultSet));
            return rentals;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch rentals", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public List<Rental> getRentalsByUser(String userId) {
        String sql = """
                SELECT id, vehicle_id, user_id, rentdatetime, returndatetime
                FROM rental WHERE user_id = ?
                """;
        List<Rental> rentals = new ArrayList<>();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) rentals.add(mapRental(resultSet));
            }
            return rentals;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch rentals for user: " + userId, e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
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
                parse(resultSet.getString("rentdatetime")),
                parse(resultSet.getString("returndatetime"))
        );
    }

    private String stringify(LocalDateTime value) { return value == null ? null : value.toString(); }
    private LocalDateTime parse(String value) { return value == null ? null : LocalDateTime.parse(value); }
}