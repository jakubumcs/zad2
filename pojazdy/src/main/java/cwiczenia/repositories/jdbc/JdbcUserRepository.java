package cwiczenia.repositories.jdbc;

import cwiczenia.models.User;
import cwiczenia.repositories.IUserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("jdbc")
public class JdbcUserRepository implements IUserRepository {

    private final DataSource dataSource;

    public JdbcUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public User getUser(String login) {
        String sql = "SELECT id, login, password_hash, role FROM users WHERE login = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) return mapUser(resultSet);
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch user by login: " + login, e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT id, login, password_hash, role FROM users";
        List<User> users = new ArrayList<>();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) users.add(mapUser(resultSet));
            return users;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch users", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void add(User user) {
        String sql = """
                INSERT INTO users (id, login, password_hash, role)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (login) DO UPDATE
                SET id = EXCLUDED.id,
                password_hash = EXCLUDED.password_hash,
                role = EXCLUDED.role
                """;
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getId());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save user: " + user.getLogin(), e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void update(User user) { add(user); }

    @Override
    public void remove(String login) {
        String sql = "DELETE FROM users WHERE login = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to remove user: " + login, e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void removeAll() {
        String sql = "DELETE FROM users";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to remove users", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getString("id"),
                resultSet.getString("login"),
                resultSet.getString("password_hash"),
                resultSet.getString("role")
        );
    }
}