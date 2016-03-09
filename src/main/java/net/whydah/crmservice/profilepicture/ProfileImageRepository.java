package net.whydah.crmservice.profilepicture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.profilepicture.model.ProfileImage;

import javax.sql.DataSource;
import java.sql.*;

@Singleton
public class ProfileImageRepository {

    private final DataSource dataSource;
    private final ObjectMapper jsonMapper;

    @Inject
    public ProfileImageRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        jsonMapper = new ObjectMapper();
    }

    private Connection getConnection(boolean autocommit) throws SQLException {
        Connection connection  = dataSource.getConnection();
        connection.setAutoCommit(autocommit);
        return connection;
    }

    private static final String SQL_RETRIEVE_PROFILEIMAGE = "SELECT profileimage, contenttype from customers WHERE customer_id = ?";
    private static final String SQL_UPDATE_PROFILEIMAGE = "UPDATE customers SET profileimage = ?, contenttype = ? WHERE customer_id = ?";


    public int deleteProfileImage(String customerRef) {
        return updateProfileImage(customerRef, null);
    }

    public int updateProfileImage(String customerRef, ProfileImage profileImage) {
        try (Connection connection = getConnection(false)) {

            PreparedStatement statement = connection.prepareCall(SQL_UPDATE_PROFILEIMAGE);

            byte[] data = null;
            String contentType = null;

            if (profileImage != null) {
                data = profileImage.getData();
                contentType = profileImage.getContentType();
            }

            statement.setObject(1, data);
            statement.setObject(2, contentType);
            statement.setString(3, customerRef);

            int affectedRows = statement.executeUpdate();
            connection.commit();
            return affectedRows;
        }  catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ProfileImage getProfileImage(String customerRef) {
        try (Connection connection = getConnection(true)) {

            PreparedStatement statement = connection.prepareCall(SQL_RETRIEVE_PROFILEIMAGE);
            statement.setString(1, customerRef);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new ProfileImage(resultSet.getBytes(1), resultSet.getString(2));
            } else {
                return null;
            }
        } catch (Throwable e) {

            throw new RuntimeException(e);
        }
    }
}
