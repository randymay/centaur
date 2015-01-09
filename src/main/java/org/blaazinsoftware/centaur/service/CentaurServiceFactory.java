package org.blaazinsoftware.centaur.service;

/**
 * @author Randy May
 *
 */
public class CentaurServiceFactory {

    /**
     * Creates a new instance of Centaur Service using the following:
     *              <code>CentaurService</code>
     *              <code>CentaurDAO</code>
     *              <code>EntityTranslator</code>
     *              <code>CentaurCache</code>
     *
     * @return          - instance of CentaurService
     */
    public static CentaurService newInstance() {
        CentaurServiceConfig config = new CentaurServiceConfig();
        config.setCentaurDAO(new DefaultCentaurDAOImpl());
        config.setEntityTranslator(new DefaultEntityTranslatorImpl());
        config.setCentaurCache(new DefaultCentaurCacheImpl());

        return CentaurServiceFactory.newInstance(config);
    }

    /**
     * Creates a new instance of Centaur Service using the following from <code>CentaurServiceConfig</code>:
     *              <code>CentaurService</code>
     *              <code>CentaurDAO</code>
     *              <code>EntityTranslator</code>
     *              <code>CentaurCache</code>
     * If any of those values are null, the appropriate default values will be used:
     *              <code>DefaultCentaurServiceImpl</code>
     *              <code>DefaultCentaurDAOImpl</code>
     *              <code>DefaultEntityTranslatorImpl</code>
     *              <code>DefaultCentaurCacheImpl</code>
     *
     * @return          - instance of CentaurService
     */
    public static CentaurService newInstance(CentaurServiceConfig config) {
        DefaultCentaurServiceImpl service = new DefaultCentaurServiceImpl();

        service.setEntityTranslator(null == config.getEntityTranslator() ? new DefaultEntityTranslatorImpl() : config.getEntityTranslator());
        service.setDao(null == config.getCentaurDAO() ? new DefaultCentaurDAOImpl() : config.getCentaurDAO());
        service.setCache(null == config.getCentaurCache() ? new DefaultCentaurCacheImpl() : config.getCentaurCache());

        return service;
    }
}
