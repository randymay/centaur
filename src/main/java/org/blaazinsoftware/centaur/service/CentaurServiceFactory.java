package org.blaazinsoftware.centaur.service;

/**
 * Created by randymay on 2014-09-11.
 */
public class CentaurServiceFactory {
    public static CentaurService newInstance() {
        CentaurServiceConfig config = new CentaurServiceConfig();
        config.setNamespace("");

        return CentaurServiceFactory.newInstance(config);
    }

    public static CentaurService newInstance(CentaurServiceConfig centaurServiceConfig) {
        CentaurServiceImpl service = new CentaurServiceImpl();
        service.setConfig(centaurServiceConfig);

        service.setDao(new CentaurDAO());

        return service;
    }
}
