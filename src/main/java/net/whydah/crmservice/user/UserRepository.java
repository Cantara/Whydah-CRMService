package net.whydah.crmservice.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.user.model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Singleton
public class UserRepository {

    private final DataSource dataSource;

    @Inject
    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createUser(User user) {
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareCall("SELECT 1").execute(); // TODO write user
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
