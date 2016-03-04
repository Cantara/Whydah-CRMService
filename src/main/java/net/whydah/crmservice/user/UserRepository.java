package net.whydah.crmservice.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.user.model.User;
import org.postgresql.util.PGobject;

import javax.sql.DataSource;
import java.sql.*;


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

    public boolean createUser(String userId, User user) throws SQLIntegrityConstraintViolationException {

        try (Connection connection = getConnection(false)) {

            user.setId(userId);

            PGobject jsonObject = new PGobject();
            jsonObject.setType("jsonb");
            jsonObject.setValue(jsonMapper.writeValueAsString(user));

            PreparedStatement statement = connection.prepareCall(SQL_CREATE_USER);
            statement.setString(1, userId);
            statement.setObject(2, jsonObject);

            boolean result = statement.execute();
            connection.commit();
            return result;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) { //SQL State=23505 -> Primary key constraint violated
                throw new SQLIntegrityConstraintViolationException(e);
            }
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public int updateUser(String userId, User user) {
        try (Connection connection = getConnection(false)) {
            user.setId(userId);

            PGobject jsonObject = new PGobject();
            jsonObject.setType("jsonb");
            jsonObject.setValue(jsonMapper.writeValueAsString(user));

            PreparedStatement statement = connection.prepareCall(SQL_UPDATE_USER);
            statement.setObject(1, jsonObject);
            statement.setString(2, userId);

            int affectedRows = statement.executeUpdate();
            connection.commit();
            return affectedRows;
        }  catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection(boolean autocommit) throws SQLException {
        Connection connection  = dataSource.getConnection();
        connection.setAutoCommit(autocommit);
        return connection;
    }

    public User getUser(String userId) {
        try (Connection connection = getConnection(true)) {

            PreparedStatement statement = connection.prepareCall(SQL_RETRIEVE_USER);
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
    public int deleteUser(String userId) {
        try (Connection connection = getConnection(false)) {
            PreparedStatement statement = connection.prepareCall(SQL_DELETE_USER);
            statement.setString(1, userId);

            int affectedRows = statement.executeUpdate();
            connection.commit();
            return affectedRows;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
