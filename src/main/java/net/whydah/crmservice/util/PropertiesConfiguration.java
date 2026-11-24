package net.whydah.crmservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * The configuration {@code Main.migrateDb()} reads, from a classpath properties
 * file layered under an optional override file.
 *
 * <p>Replaces Constretto, which was used here only to layer two properties
 * files. Precedence is Constretto's, as measured against constretto-core 2.2.3
 * for the Cantara Jakarta migrations, so behaviour is unchanged:
 * <ol>
 *   <li>a JVM system property ({@code -Dkey=value}) wins,</li>
 *   <li>then an environment variable with exactly the same name,</li>
 *   <li>then the override file, if present,</li>
 *   <li>then the classpath file.</li>
 * </ol>
 * A missing override file is normal. Asking for a key that is set nowhere
 * throws, as Constretto did.
 */
public final class PropertiesConfiguration {

    private static final Logger log = LoggerFactory.getLogger(PropertiesConfiguration.class);

    private final Properties properties;

    private PropertiesConfiguration(Properties properties) {
        this.properties = properties;
    }

    public static PropertiesConfiguration load(String classpathResource, String overrideFile) {
        Properties props = new Properties();
        try (InputStream in = PropertiesConfiguration.class.getClassLoader()
                .getResourceAsStream(classpathResource)) {
            if (in != null) {
                props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            } else {
                log.warn("No {} on the classpath", classpathResource);
            }
        } catch (IOException e) {
            log.error("Could not read {} from the classpath", classpathResource, e);
        }

        Path override = Path.of(overrideFile);
        if (Files.isReadable(override)) {
            try (Reader r = Files.newBufferedReader(override, StandardCharsets.UTF_8)) {
                props.load(r);      // loaded second: overrides win
                log.info("Applied configuration overrides from {}", override);
            } catch (IOException e) {
                log.error("Could not read overrides from {}", override, e);
            }
        }
        return new PropertiesConfiguration(props);
    }

    private String lookup(String key) {
        String sys = System.getProperty(key);
        if (sys != null) {
            return sys;
        }
        String env = System.getenv(key);
        return env != null ? env : properties.getProperty(key);
    }

    private String required(String key) {
        String value = lookup(key);
        if (value == null) {
            throw new IllegalStateException("Missing configuration property: " + key);
        }
        return value;
    }

    public String getString(String key) {
        return required(key);
    }

    public int getInt(String key) {
        return Integer.parseInt(required(key).trim());
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(required(key).trim());
    }
}
