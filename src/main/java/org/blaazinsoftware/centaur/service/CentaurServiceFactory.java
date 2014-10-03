package org.blaazinsoftware.centaur.service;

/**
 * @author Randy May <a href="www.blaazinsoftware.com">Blaazin Software Consulting, Inc.</a>
 */
public class CentaurServiceFactory {
    public static CentaurService newInstance() {
        CentaurServiceConfig config = new CentaurServiceConfig();
        config.setCentaurDAO(new DefaultCentaurDAO());
        config.setEntityTranslator(new DefaultEntityTranslator());

        return CentaurServiceFactory.newInstance(config);
    }

    public static CentaurService newInstance(CentaurServiceConfig config) {
        CentaurServiceImpl service = new CentaurServiceImpl();

        service.setEntityTranslator(null == config.getEntityTranslator() ? new DefaultEntityTranslator() : config.getEntityTranslator());
        service.setDao(null == config.getCentaurDAO() ? new DefaultCentaurDAO() : config.getCentaurDAO());

        return service;
    }
}
