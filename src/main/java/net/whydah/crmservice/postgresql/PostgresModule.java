package net.whydah.crmservice.postgresql;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import net.whydah.crmservice.util.DatabaseMigrationHelper;

import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class PostgresModule extends AbstractModule {
   
	@Override
    protected void configure() {
		
    }

    @Provides
    DataSource dataSource(@Named("postgres.server") String serverName,
                          @Named("postgres.port") int portNumber,
                          @Named("postgres.db") String databaseName,
                          @Named("postgres.user") String user,
                          @Named("postgres.password") String password) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();       
        dataSource.setServerName(serverName);
        dataSource.setPortNumber(portNumber);
        dataSource.setDatabaseName(databaseName);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        return dataSource;
    }
}
