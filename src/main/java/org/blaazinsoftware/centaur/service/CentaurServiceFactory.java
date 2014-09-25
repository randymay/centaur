package org.blaazinsoftware.centaur.service;

/**
 * Created by randymay on 2014-09-11.
 */
public class CentaurServiceFactory {
    public static CentaurService newInstance() {
        CentaurServiceImpl service = new CentaurServiceImpl();

        service.setDao(new CentaurDAO());

        return service;
    }
}
