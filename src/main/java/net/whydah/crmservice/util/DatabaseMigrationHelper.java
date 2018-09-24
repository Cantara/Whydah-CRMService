package net.whydah.crmservice.util;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ensure database has the necessary DDL and all migrations have been applied.
 *
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a>
 */
public class DatabaseMigrationHelper {
    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationHelper.class);

    private Flyway flyway;
    private String dbUrl;

    public DatabaseMigrationHelper(DataSource dataSource, String location) {
        flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations(location);
        //flyway.setBaselineOnMigrate(true);
    }


    public void upgradeDatabase() {
        log.info("Upgrading database with url={} using migration files from {}", dbUrl, flyway.getLocations());
        try {
            flyway.migrate();
        } catch (FlywayException e) {
            log.error("Database upgrade failed using " + dbUrl, e);
//            throw new RuntimeException("Database upgrade failed using " + dbUrl, e);
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
