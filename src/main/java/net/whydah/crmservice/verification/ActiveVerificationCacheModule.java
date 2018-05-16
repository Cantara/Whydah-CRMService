package net.whydah.crmservice.verification;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

public class ActiveVerificationCacheModule extends AbstractModule {
    @Override
    protected void configure() {

    }


    @Provides
    ActiveVerificationCache activeVerificationCache(@Named("hazelcast.config")String hazelcastConfig, @Named("gridprefix") String gridPrefix) {
    	return new ActiveVerificationCache(hazelcastConfig, gridPrefix);
    }
}
