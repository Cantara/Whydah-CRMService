package net.whydah.crmservice.profilepicture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.profilepicture.model.ProfilePicture;

import javax.sql.DataSource;
import java.sql.*;

@Singleton
public class ProfilepictureRepository {

    private final DataSource dataSource;
    private final ObjectMapper jsonMapper;

    @Inject
    public ProfilepictureRepository(DataSource dataSource) {
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


    public int deleteProfileimage(String customerRef) {
        return updateProfileimage(customerRef, null);
    }

    public int updateProfileimage(String customerRef, ProfilePicture profilePicture) {
        try (Connection connection = getConnection(false)) {

            PreparedStatement statement = connection.prepareCall(SQL_UPDATE_PROFILEIMAGE);

            byte[] imageData = null;
            String contentType = null;

            if (profilePicture != null) {
                imageData = profilePicture.getImageData();
                contentType = profilePicture.getContentType();
            }

            statement.setObject(1, imageData);
            statement.setObject(2, contentType);
            statement.setString(3, customerRef);

            int affectedRows = statement.executeUpdate();
            connection.commit();
            return affectedRows;
        }  catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ProfilePicture getProfileimage(String customerRef) {
        try (Connection connection = getConnection(true)) {

            PreparedStatement statement = connection.prepareCall(SQL_RETRIEVE_PROFILEIMAGE);
            statement.setString(1, customerRef);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new ProfilePicture(resultSet.getBytes(1), resultSet.getString(2));
            } else {
                return null;
            }
        } catch (Throwable e) {

            throw new RuntimeException(e);
        }
    }
}
