package net.whydah.crmservice.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.user.model.Customer;
import org.postgresql.util.PGobject;

import javax.sql.DataSource;
import java.sql.*;


@Singleton
public class CustomerRepository {

    private final DataSource dataSource;
    private final ObjectMapper jsonMapper;

    @Inject
    public CustomerRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        jsonMapper = new ObjectMapper();
    }

    private static final String SQL_CREATE_USER = "INSERT INTO customers (customer_id, data) values(?, ?)";
    private static final String SQL_RETRIEVE_USER = "SELECT data from customers WHERE customer_id = ?";
    private static final String SQL_UPDATE_USER = "UPDATE customers SET data = ? WHERE customer_id = ?";
    private static final String SQL_DELETE_USER = "DELETE FROM customers WHERE customer_id = ?";

    public boolean createCustomer(String customerRef, Customer customer) throws SQLIntegrityConstraintViolationException {

        try (Connection connection = getConnection(false)) {

            customer.setId(customerRef);

            PGobject jsonObject = new PGobject();
            jsonObject.setType("jsonb");
            jsonObject.setValue(jsonMapper.writeValueAsString(customer));

            PreparedStatement statement = connection.prepareCall(SQL_CREATE_USER);
            statement.setString(1, customerRef);
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

    public int updateCustomer(String customerRef, Customer customer) {
        try (Connection connection = getConnection(false)) {
            customer.setId(customerRef);

            PGobject jsonObject = new PGobject();
            jsonObject.setType("jsonb");
            jsonObject.setValue(jsonMapper.writeValueAsString(customer));

            PreparedStatement statement = connection.prepareCall(SQL_UPDATE_USER);
            statement.setObject(1, jsonObject);
            statement.setString(2, customerRef);

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

    public Customer getCustomer(String customerRef) {
        try (Connection connection = getConnection(true)) {

            PreparedStatement statement = connection.prepareCall(SQL_RETRIEVE_USER);
            statement.setString(1, customerRef);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                PGobject object = resultSet.getObject(1, PGobject.class);
                return jsonMapper.readValue(object.getValue(), Customer.class);
            } else {
                return null;
            }
        } catch (Throwable e) {

            throw new RuntimeException(e);
        }
    }

    public int deleteCustomer(String customerRef) {
        try (Connection connection = getConnection(false)) {
            PreparedStatement statement = connection.prepareCall(SQL_DELETE_USER);
            statement.setString(1, customerRef);

            int affectedRows = statement.executeUpdate();
            connection.commit();
            return affectedRows;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
