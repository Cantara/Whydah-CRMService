package net.whydah.crmservice.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import ratpack.exec.Promise;
import ratpack.health.HealthCheck;
import ratpack.registry.Registry;

@Singleton
public class UserHealthCheck implements HealthCheck {

    private final String nameOfThisHealthcheck;

    @Inject
    public UserHealthCheck(@Named("healthcheck.user.name") String nameOfThisHealthcheck) {
        this.nameOfThisHealthcheck = nameOfThisHealthcheck;
    }

    @Override
    public String getName() {
        return nameOfThisHealthcheck;
    }

    @Override
    public Promise<Result> check(Registry registry) throws Exception {
        return Promise.value(HealthCheck.Result.healthy());
    }
}
