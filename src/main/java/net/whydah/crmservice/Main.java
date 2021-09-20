package net.whydah.crmservice;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.Injector;
import net.whydah.crmservice.configuration.HazelcastConfig;
import net.whydah.crmservice.customer.CreateCustomerHandler;
import net.whydah.crmservice.customer.CustomerModule;
import net.whydah.crmservice.customer.DeleteCustomerHandler;
import net.whydah.crmservice.customer.GetCustomerHandler;
import net.whydah.crmservice.customer.UpdateCustomerHandler;
import net.whydah.crmservice.health.GetHealthHandler;
import net.whydah.crmservice.health.HealthModule;
import net.whydah.crmservice.postgresql.PostgresModule;
import net.whydah.crmservice.profilepicture.CreateProfileImageHandler;
import net.whydah.crmservice.profilepicture.DeleteProfileImageHandler;
import net.whydah.crmservice.profilepicture.GetProfileImageHandler;
import net.whydah.crmservice.profilepicture.UpdateProfileImageHandler;
import net.whydah.crmservice.security.SecurityHandler;
import net.whydah.crmservice.security.SecurityModule;
import net.whydah.crmservice.util.DatabaseMigrationHelper;
import net.whydah.crmservice.util.MailModule;
import net.whydah.crmservice.util.ReporterModule;
import net.whydah.crmservice.util.SecurityTokenServiceModule;
import net.whydah.crmservice.util.SmsModule;
import net.whydah.crmservice.verification.ActiveVerificationCache;
import net.whydah.crmservice.verification.EmailVerificationHandler;
import net.whydah.crmservice.verification.PhoneVerificationHandler;
import no.cantara.ratpack.config.RatpackConfigs;
import no.cantara.ratpack.config.RatpackGuiceConfigModule;
import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.model.Resource;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.dropwizard.metrics.DropwizardMetricsConfig;
import ratpack.dropwizard.metrics.DropwizardMetricsModule;
import ratpack.dropwizard.metrics.MetricsWebsocketBroadcastHandler;
import ratpack.error.ClientErrorHandler;
import ratpack.error.internal.DefaultDevelopmentErrorHandler;
import ratpack.func.Action;
import ratpack.func.Function;
import ratpack.guice.Guice;
import ratpack.handling.Chain;
import ratpack.handling.Handler;
import ratpack.health.HealthCheckHandler;
import ratpack.registry.Registry;
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfig;
import ratpack.server.ServerConfigBuilder;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static final String APPLICATION_NAME = "Whydah-CRMService";
    public static final int HTTP_PORT = 12121;
    public static final String CONTEXT_ROOT = "/crmservice";
    public static final String DEFAULT_CONFIGURATION_RESOURCE_PATH = "appconfig/crmservice.properties";
    public static final String OVERRIDE_CONFIGURATION_FILE_PATH = "crmservice.properties";

    private ServerConfig serverConfig;

    public static void main(String... args) throws Exception {
        new Main().start();
    }

    public void start() throws Exception {

        Action<ServerConfigBuilder> configurationBuilder = RatpackConfigs
                .configuration(APPLICATION_NAME, HTTP_PORT, DEFAULT_CONFIGURATION_RESOURCE_PATH, OVERRIDE_CONFIGURATION_FILE_PATH);

        serverConfig = ServerConfig.of(configurationBuilder);

        migrateDb();

        RatpackServer ratpackServer = RatpackServer.of(server -> server
                .serverConfig(serverConfig)
                .registry(registry())
                .handlers(rootChain(CONTEXT_ROOT))
        );

        ratpackServer.start();
    }

    public void migrateDb() {
    	 final ConstrettoConfiguration configuration = new ConstrettoBuilder()
                 .createPropertiesStore()
                 .addResource(Resource.create("classpath:" + DEFAULT_CONFIGURATION_RESOURCE_PATH))
                 .addResource(Resource.create("file:./" + OVERRIDE_CONFIGURATION_FILE_PATH))
                 .done()
                 .getConfiguration();



         migrateDb(configuration.evaluateToString("postgres.server"),
        		 configuration.evaluateToInt("postgres.port"),
        		 configuration.evaluateToString("postgres.db"),
        		 configuration.evaluateToString("postgres.user"),
        		 configuration.evaluateToString("postgres.password"),
                 configuration.evaluateToBoolean("flyway.baseline-on-migrate"),
                 configuration.evaluateToString("flyway.baseline-version"));


	}

    public void migrateDb(String server, int port, String db, String user, String pwd, boolean baselineOnMigrate, String baselineVersion) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(server);
        dataSource.setPortNumber(port);
        dataSource.setDatabaseName(db);
        dataSource.setUser(user);
        dataSource.setPassword(pwd);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("username", user);

        DatabaseMigrationHelper migrationHelper = new DatabaseMigrationHelper(
                dataSource, "db/migration/postgresql", placeholders, baselineOnMigrate, baselineVersion);
        migrationHelper.upgradeDatabase();


    }

	private Function<Registry, Registry> registry() {
        return Guice.registry(bindings -> bindings
                .module(new RatpackGuiceConfigModule(bindings.getServerConfig()))
                .module(PostgresModule.class)
                .module(SecurityTokenServiceModule.class)
                .module(HealthModule.class)
                .module(CustomerModule.class)
                .module(SecurityModule.class)
                .module(SmsModule.class)
                .module(MailModule.class)
                .module(ReporterModule.class)
               // .module(ActiveVerificationCacheModule.class)
                .moduleConfig(DropwizardMetricsModule.class, new DropwizardMetricsConfig()
                                .jmx(jmxConfig -> jmxConfig.enable(true))
                                .jvmMetrics(true)
                                .webSocket(websocketConfig -> {
                                })
                )
                .bind(ClientErrorHandler.class, DefaultDevelopmentErrorHandler.class)
                .bind(ActiveVerificationCache.class)
                .bindInstance(HazelcastConfig.class, serverConfig.get("/hazelcast", HazelcastConfig.class))
        );
    }

    private Action<Chain> rootChain(String contextRoot) {
        if (contextRoot.startsWith("/")) {
            contextRoot = contextRoot.substring(1);
        }
        final String noSlashContextRoot = contextRoot;
        return rootChain -> rootChain
                .all(requestCountMetricsHandler())
                .prefix(noSlashContextRoot, applicationChain())
                .get(chain -> chain.redirect(301, "/" + noSlashContextRoot))
                .get("favicon.ico", sendFileHandler("assets/ico/3dlb-3d-Lock.ico"))
                .all(chain -> chain.notFound());
    }

    private Action<Chain> applicationChain() {
        return appChain -> appChain
                .all(requestCountMetricsHandler())
                .prefix("admin", chain -> {
                    chain.get("metrics", new MetricsWebsocketBroadcastHandler());
                    chain.get("health/:name?", new HealthCheckHandler());
                })
                .prefix(":apptokenId/:userTokenId", chain -> {
                    chain.all(appChain.getRegistry().get(SecurityHandler.class))
                            .path("customer/:customerRef", ctx -> {
                                ctx.byMethod(m -> m.
                                                get(() -> ctx.get(Injector.class).getInstance(GetCustomerHandler.class).handle(ctx)).
                                                post(() -> ctx.get(Injector.class).getInstance(CreateCustomerHandler.class).handle(ctx)).
                                                put(() -> ctx.get(Injector.class).getInstance(UpdateCustomerHandler.class).handle(ctx)).
                                                delete(() -> ctx.get(Injector.class).getInstance(DeleteCustomerHandler.class).handle(ctx))
                                );
                            })
                            .path("customer/:customerRef/image", ctx -> {
                                ctx.byMethod(m -> m.
                                                get(() -> ctx.get(Injector.class).getInstance(GetProfileImageHandler.class).handle(ctx)).
                                                post(() -> ctx.get(Injector.class).getInstance(CreateProfileImageHandler.class).handle(ctx)).
                                                put(() -> ctx.get(Injector.class).getInstance(UpdateProfileImageHandler.class).handle(ctx)).
                                                delete(() -> ctx.get(Injector.class).getInstance(DeleteProfileImageHandler.class).handle(ctx))
                                );
                            })
                            .path("customer/:customerRef/verify/phone", ctx -> {
                                ctx.byMethod(m ->
                                        m.get(() -> ctx.get(Injector.class).getInstance(PhoneVerificationHandler.class).handle(ctx)));
                            })
                            .path("customer/:customerRef/verify/email", ctx -> {
                                ctx.byMethod(m ->
                                        m.get(() -> ctx.get(Injector.class).getInstance(EmailVerificationHandler.class).handle(ctx)));
                            })
                            .prefix("customer", postChain -> {
                                postChain.post(chain.getRegistry().get(Injector.class).getInstance(CreateCustomerHandler.class));
                            });
                })
                .get("health", ctx -> {
                    ctx.byMethod(m ->
                            m.get(() -> ctx.get(Injector.class).getInstance(GetHealthHandler.class).handle(ctx)));
                })//new GetHealthHandler())
                .get("favicon.ico", sendFileHandler("assets/ico/3dlb-3d-Lock.ico"))

                // redirect index* to root path
                .prefix("index", chain -> chain.redirect(301, "/"));
    }

    private static Handler sendFileHandler(String path) {
        return ctx -> ctx.getResponse().sendFile(Paths.get(Main.class.getClassLoader().getResource(path).toURI()));
    }


    private static Handler requestCountMetricsHandler() {
        return ctx -> {
            MetricRegistry metricRegistry = ctx.get(MetricRegistry.class);
            metricRegistry.counter("request-count").inc();
            ctx.next();
        };
    }
}
