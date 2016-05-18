package net.whydah.crmservice.util;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import org.valuereporter.agent.activity.ObservedActivityDistributer;
import org.valuereporter.agent.http.HttpObservationDistributer;

public class ReporterModule extends AbstractModule {
    @Override
    protected void configure() {

    }


    @Provides
    void reporterClient(@Named("valuereporter.host") String reporterHost,
                          @Named("valuereporter.port") String reporterPort,
                          @Named("applicationname") String prefix,
                          @Named("valuereporter.activity.batchsize") int cacheSize,
                          @Named("valuereporter.activity.postintervalms") int forwardInterval) {

        //Start Valuereporter event distributer.
        new Thread(new ObservedActivityDistributer(reporterHost, reporterPort, prefix, cacheSize, forwardInterval)).start();
        new Thread(new HttpObservationDistributer(reporterHost, reporterPort, prefix)).start();
    }
}
