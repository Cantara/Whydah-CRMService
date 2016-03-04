package net.whydah.crmservice.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.user.model.User;
import org.postgresql.ds.common.PGObjectFactory;
import org.postgresql.util.PGobject;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


@Singleton
public class UserRepository {

    private final DataSource dataSource;
    private final ObjectMapper jsonMapper;

    @Inject
    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        jsonMapper = new ObjectMapper();
    }

    private static final String SQL_CREATE_USER = "INSERT INTO users (user_id, data) values(?, ?)";
    private static final String SQL_RETRIEVE_USER = "SELECT data from users WHERE user_id = ?";
    private static final String SQL_UPDATE_USER = "UPDATE users SET data = ? WHERE user_id = ?";
    private static final String SQL_DELETE_USER = "DELETE FROM users WHERE user_id = ?";

    public boolean createUser(String userId, User user) {
        try (Connection connection = getConnection()) {

            user.setId(userId);

            PGobject jsonObject = new PGobject();
            jsonObject.setType("jsonb");
            jsonObject.setValue(jsonMapper.writeValueAsString(user));

            CallableStatement statement = connection.prepareCall(SQL_CREATE_USER);
            statement.setString(1, userId);
            statement.setObject(2, jsonObject);

            boolean result = statement.execute();
            connection.commit();
            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateUser(String userId, User user) {
        try (Connection connection = getConnection()) {
            user.setId(userId);

            PGobject jsonObject = new PGobject();
            jsonObject.setType("jsonb");
            jsonObject.setValue(jsonMapper.writeValueAsString(user));

            CallableStatement statement = connection.prepareCall(SQL_UPDATE_USER);
            statement.setObject(1, jsonObject);
            statement.setString(2, userId);

            boolean result = statement.execute();
            connection.commit();
            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        Connection connection  = dataSource.getConnection();
        connection.setAutoCommit(false);
        return connection;
    }

    public User getUser(String userId) {
        try (Connection connection = getConnection()) {

            CallableStatement statement = connection.prepareCall(SQL_RETRIEVE_USER);
            statement.setString(1, userId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                PGobject object = resultSet.getObject(1, PGobject.class);

                return jsonMapper.readValue(object.getValue(), User.class);
            } else {
                return null;
            }
        } catch (Throwable e) {

            throw new RuntimeException(e);
        }
    }
    public boolean deleteUser(String userId) {
        try (Connection connection = getConnection()) {
            CallableStatement statement = connection.prepareCall(SQL_DELETE_USER);
            statement.setString(1, userId);

            boolean result = statement.execute();
            connection.commit();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
