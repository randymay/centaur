package org.blaazinsoftware.centaur.service;

/**
 * @author Randy May
 */
public class CentaurServiceConfig {
    private CentaurDAO centaurDAO;
    private EntityTranslator entityTranslator;
    private CentaurCache centaurCache;

    public CentaurDAO getCentaurDAO() {
        return centaurDAO;
    }

    public void setCentaurDAO(CentaurDAO centaurDAO) {
        this.centaurDAO = centaurDAO;
    }

    public EntityTranslator getEntityTranslator() {
        return entityTranslator;
    }

    public void setEntityTranslator(EntityTranslator entityTranslator) {
        this.entityTranslator = entityTranslator;
    }

    public CentaurCache getCentaurCache() {
        return centaurCache;
    }

    public void setCentaurCache(CentaurCache centaurCache) {
        this.centaurCache = centaurCache;
    }
}
