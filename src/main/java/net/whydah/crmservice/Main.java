package net.whydah.crmservice;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.Injector;
import net.whydah.crmservice.postgresql.PostgresModule;
import net.whydah.crmservice.customer.*;
import net.whydah.crmservice.profilepicture.CreateProfileImageHandler;
import net.whydah.crmservice.profilepicture.DeleteProfileImageHandler;
import net.whydah.crmservice.profilepicture.GetProfileImageHandler;
import net.whydah.crmservice.profilepicture.UpdateProfileImageHandler;
import no.cantara.ratpack.config.RatpackConfigs;
import no.cantara.ratpack.config.RatpackGuiceConfigModule;
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

import java.nio.file.Paths;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static final String APPLICATION_NAME = "Whydah-CRMService";
    public static final int HTTP_PORT = 12121;
    public static final String CONTEXT_ROOT = "/crmservice";
    public static final String DEFAULT_CONFIGURATION_RESOURCE_PATH = "appconfig/crmservice.properties";
    public static final String OVERRIDE_CONFIGURATION_FILE_PATH = "crmservice.properties";

    public static void main(String... args) throws Exception {
        new Main().start();
    }

    public void start() throws Exception {
        RatpackServer.start(server -> server
                .serverConfig(RatpackConfigs.configuration(APPLICATION_NAME, HTTP_PORT, DEFAULT_CONFIGURATION_RESOURCE_PATH, OVERRIDE_CONFIGURATION_FILE_PATH))
                .registry(registry())
                .handlers(rootChain(CONTEXT_ROOT))
        );
    }

    private Function<Registry, Registry> registry() {
        return Guice.registry(bindings -> bindings
                .module(new RatpackGuiceConfigModule(bindings.getServerConfig()))
                .module(PostgresModule.class)
                .module(CustomerModule.class)
                .moduleConfig(DropwizardMetricsModule.class, new DropwizardMetricsConfig()
                                .jmx(jmxConfig -> jmxConfig.enable(true))
                                .jvmMetrics(true)
                                .webSocket(websocketConfig -> {
                                })
                )
                .bind(ClientErrorHandler.class, DefaultDevelopmentErrorHandler.class)
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
                .path("customer/:customerRef/image", ctx -> {
                    ctx.byMethod(m -> m.
                            get(() -> ctx.get(Injector.class).getInstance(GetProfileImageHandler.class).handle(ctx)).
                            post(() -> ctx.get(Injector.class).getInstance(CreateProfileImageHandler.class).handle(ctx)).
                            put(() -> ctx.get(Injector.class).getInstance(UpdateProfileImageHandler.class).handle(ctx)).
                            delete(() -> ctx.get(Injector.class).getInstance(DeleteProfileImageHandler.class).handle(ctx))
                    );
                })
                .path("customer/:customerRef", ctx -> {
                    ctx.byMethod(m -> m.
                            get(() -> ctx.get(Injector.class).getInstance(GetCustomerHandler.class).handle(ctx)).
                            post(() -> ctx.get(Injector.class).getInstance(CreateCustomerHandler.class).handle(ctx)).
                            put(() -> ctx.get(Injector.class).getInstance(UpdateCustomerHandler.class).handle(ctx)).
                            delete(() -> ctx.get(Injector.class).getInstance(DeleteCustomerHandler.class).handle(ctx))
                    );
                })
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
