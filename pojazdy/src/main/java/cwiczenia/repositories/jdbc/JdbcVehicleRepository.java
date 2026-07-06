package cwiczenia.repositories.jdbc;

import cwiczenia.models.Vehicle;
import cwiczenia.repositories.IVehicleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@Profile("jdbc")
public class JdbcVehicleRepository implements IVehicleRepository {

    private final DataSource dataSource;

    public JdbcVehicleRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void add(Vehicle vehicle) {
        String vehicleSql = """
                INSERT INTO vehicle (id, category, brand, model, year, plate, price, rented, location_name, latitude, longitude)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE
                SET category = EXCLUDED.category,
                    brand = EXCLUDED.brand,
                    model = EXCLUDED.model,
                    year = EXCLUDED.year,
                    plate = EXCLUDED.plate,
                    price = EXCLUDED.price,
                    rented = EXCLUDED.rented,
                    location_name = EXCLUDED.location_name,
                    latitude = EXCLUDED.latitude,
                    longitude = EXCLUDED.longitude
                """;
        String deleteAttributesSql = "DELETE FROM vehicle_attribute WHERE vehicle_id = ?";
        String insertAttributeSql = """
                INSERT INTO vehicle_attribute (vehicle_id, attr_key, attr_value)
                VALUES (?, ?, ?)
                """;

        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            try (PreparedStatement vehicleStatement = connection.prepareStatement(vehicleSql);
                 PreparedStatement deleteAttributesStatement = connection.prepareStatement(deleteAttributesSql);
                 PreparedStatement insertAttributeStatement = connection.prepareStatement(insertAttributeSql)) {

                fillVehicleStatement(vehicleStatement, vehicle);
                vehicleStatement.executeUpdate();

                deleteAttributesStatement.setString(1, vehicle.getId());
                deleteAttributesStatement.executeUpdate();

                for (Map.Entry<String, Object> entry : safeAttributes(vehicle).entrySet()) {
                    insertAttributeStatement.setString(1, vehicle.getId());
                    insertAttributeStatement.setString(2, entry.getKey());
                    insertAttributeStatement.setString(3, entry.getValue() == null ? null : String.valueOf(entry.getValue()));
                    insertAttributeStatement.addBatch();
                }
                insertAttributeStatement.executeBatch();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save vehicle: " + vehicle.getId(), e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void remove(String id) {
        String sql = "DELETE FROM vehicle WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to remove vehicle: " + id, e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void removeAll() {
        String deleteAttributesSql = "DELETE FROM vehicle_attribute";
        String deleteVehiclesSql = "DELETE FROM vehicle";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement deleteAttributesStatement = connection.prepareStatement(deleteAttributesSql);
             PreparedStatement deleteVehiclesStatement = connection.prepareStatement(deleteVehiclesSql)) {
            deleteAttributesStatement.executeUpdate();
            deleteVehiclesStatement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to remove vehicles", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public Vehicle getVehicle(String id) {
        String sql = "SELECT id, category, brand, model, year, plate, price, rented, location_name, latitude, longitude FROM vehicle WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) return null;
                Vehicle vehicle = mapVehicle(resultSet);
                vehicle.setAttributes(loadAttributes(connection, vehicle.getId()));
                return vehicle;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch vehicle: " + id, e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public List<Vehicle> getVehicles() {
        String sql = "SELECT id, category, brand, model, year, plate, price, rented, location_name, latitude, longitude FROM vehicle";
        List<Vehicle> vehicles = new ArrayList<>();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) vehicles.add(mapVehicle(resultSet));
            Map<String, Map<String, Object>> attributesByVehicleId = loadAttributesForVehicles(connection);
            for (Vehicle vehicle : vehicles) {
                vehicle.setAttributes(attributesByVehicleId.getOrDefault(vehicle.getId(), new HashMap<>()));
            }
            return vehicles;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch vehicles", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void update(Vehicle vehicle) { add(vehicle); }

    private void fillVehicleStatement(PreparedStatement statement, Vehicle vehicle) throws SQLException {
        statement.setString(1, vehicle.getId());
        statement.setString(2, vehicle.getCategory());
        statement.setString(3, vehicle.getBrand());
        statement.setString(4, vehicle.getModel());
        statement.setInt(5, vehicle.getYear());
        statement.setString(6, vehicle.getPlate());
        statement.setDouble(7, vehicle.getPrice());
        statement.setBoolean(8, vehicle.isRented());
        statement.setString(9, vehicle.getLocationName());
        statement.setDouble(10, vehicle.getLatitude());
        statement.setDouble(11, vehicle.getLongitude());
    }

    private Vehicle mapVehicle(ResultSet resultSet) throws SQLException {
        return new Vehicle(
                resultSet.getString("id"),
                resultSet.getString("category"),
                resultSet.getString("brand"),
                resultSet.getString("model"),
                resultSet.getInt("year"),
                resultSet.getString("plate"),
                resultSet.getDouble("price"),
                resultSet.getBoolean("rented"),
                resultSet.getString("location_name"),
                resultSet.getDouble("latitude"),
                resultSet.getDouble("longitude"),
                new HashMap<>()
        );
    }

    private Map<String, Object> loadAttributes(Connection connection, String vehicleId) throws SQLException {
        String sql = "SELECT attr_key, attr_value FROM vehicle_attribute WHERE vehicle_id = ?";
        Map<String, Object> attributes = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, vehicleId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    attributes.put(resultSet.getString("attr_key"), resultSet.getString("attr_value"));
                }
            }
        }
        return attributes;
    }

    private Map<String, Map<String, Object>> loadAttributesForVehicles(Connection connection) throws SQLException {
        String sql = "SELECT vehicle_id, attr_key, attr_value FROM vehicle_attribute";
        Map<String, Map<String, Object>> attributesByVehicleId = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                attributesByVehicleId
                        .computeIfAbsent(resultSet.getString("vehicle_id"), ignored -> new LinkedHashMap<>())
                        .put(resultSet.getString("attr_key"), resultSet.getString("attr_value"));
            }
        }
        return attributesByVehicleId;
    }

    private Map<String, Object> safeAttributes(Vehicle vehicle) {
        return vehicle.getAttributes() == null ? Map.of() : vehicle.getAttributes();
    }
}