package org.blaazinsoftware.centaur.service;

/**
 * @author Randy May
 *
 */
public class CentaurServiceFactory {

    /**
     * Creates a new instance of Centaur Service using <code>DefaultCentaurService</code>,
     * <code>DefaultCentaurDAO</code>, and <code>DefaultEntityTranslator</code>
     *
     * @return          - instance of CentaurService
     */
    public static CentaurService newInstance() {
        CentaurServiceConfig config = new CentaurServiceConfig();
        config.setCentaurDAO(new DefaultCentaurDAO());
        config.setEntityTranslator(new DefaultEntityTranslator());

        return CentaurServiceFactory.newInstance(config);
    }

    /**
     * Creates a new instance of Centaur Service using the <code>CentaurService</code>,
     * <code>CentaurDAO</code>, and <code>EntityTranslator</code> from the given
     * <code>CentaurServiceConfig</code>.  If any of those values are null, the appropriate
     * <code>DefaultCentaurService</code>, <code>DefaultCentaurDAO</code>, or <code>DefaultEntityTranslator</code>
     * will be used
     *
     * @return          - instance of CentaurService
     */
    public static CentaurService newInstance(CentaurServiceConfig config) {
        DefaultCentaurServiceImpl service = new DefaultCentaurServiceImpl();

        service.setEntityTranslator(null == config.getEntityTranslator() ? new DefaultEntityTranslator() : config.getEntityTranslator());
        service.setDao(null == config.getCentaurDAO() ? new DefaultCentaurDAO() : config.getCentaurDAO());

        return service;
    }
}
