package org.blaazinsoftware.centaur.service;

/**
 * @author Randy May <a href="www.blaazinsoftware.com">Blaazin Software Consulting, Inc.</a>
 */
public class CentaurServiceConfig {
    private CentaurDAO centaurDAO;
    private EntityTranslator entityTranslator;

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
}
