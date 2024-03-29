package net.whydah.crmservice.util;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Ensure database has the necessary DDL and all migrations have been applied.
 *
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a>
 */
public class DatabaseMigrationHelper {
    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationHelper.class);

    private Flyway flyway;

    public DatabaseMigrationHelper(DataSource dataSource, String location, Map<String, String> placeholders, boolean baselineOnMigrate, String baselineVersion) {
        FluentConfiguration fluentConfiguration = Flyway.configure()
                .baselineOnMigrate(baselineOnMigrate);
        if (baselineOnMigrate) {
            fluentConfiguration.baselineVersion(baselineVersion);
        }
        flyway = fluentConfiguration
                .placeholders(placeholders)
                .locations(location)
                .dataSource(dataSource).load();
    }


    public void upgradeDatabase() {
        log.info("Upgrading database using migration files from {}", flyway.getConfiguration().getLocations());
        try {
            flyway.migrate();
        } catch (FlywayException e) {
            log.error("Database migration failed", e);
            // throw new RuntimeException("Database upgrade failed using " + dbUrl, e);
        }
    }

    //used by tests
    public void cleanDatabase() {
        try {
            flyway.clean();
        } catch (Exception e) {
            throw new RuntimeException("Database cleaning failed.", e);
        }
    }
}
