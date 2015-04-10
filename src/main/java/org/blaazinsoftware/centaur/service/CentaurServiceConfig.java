package org.blaazinsoftware.centaur.service;

/**
 * @author Randy May
 */
public class CentaurServiceConfig {
    private CentaurDAO centaurDAO;
    private EntityTranslator entityTranslator;
    private DocumentTranslator documentTranslator;
    private CentaurCache centaurCache;
    private CentaurIndex centaurIndex;

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

    public CentaurIndex getCentaurIndex() {
        return centaurIndex;
    }

    public void setCentaurIndex(CentaurIndex centaurIndex) {
        this.centaurIndex = centaurIndex;
    }

    public DocumentTranslator getDocumentTranslator() {
        return documentTranslator;
    }

    public void setDocumentTranslator(DocumentTranslator documentTranslator) {
        this.documentTranslator = documentTranslator;
    }
}
