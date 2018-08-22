package net.whydah.crmservice.configuration;

/**
 * @author <a href="mailto:haakon@nymomatland.com">Håkon Nymo Matland</a> 2018-08-22.
 *
 * This file is part of automated properties deserialization part of Ratpack.
 * Struggled to find similar means to @Named for classes that implements ratpack.service.Service.
 *
 */
public class HazelcastConfig {
    private String filename;
    private String gridprefix;

    public String getFilename() {
        return filename;
    }

    public String getGridprefix() {
        return gridprefix;
    }


}
