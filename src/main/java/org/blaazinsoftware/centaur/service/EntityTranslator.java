package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import org.blaazinsoftware.centaur.CentaurException;

/**
 * @author Randy May
 */
public interface EntityTranslator {

    /**
     * Translates the given <code>T</code> to <code>Entity</code>
     *
     * @param object        - Object to translate
     *
     * @return              - Entity representation
     *
     * @throws CentaurException
     */
    public <T> Entity toEntity(final T object) throws CentaurException;

    /**
     * Translates the given <code>T</code> to <code>Entity</code>
     *
     * @param object        - Object to translate
     * @param parentKey     - The parent of the <code>Entity</code>, null if non-existent
     *
     * @return              - Entity representation
     *
     * @throws CentaurException
     */
    public <T> Entity toEntity(final T object, Key parentKey) throws CentaurException;

    /**
     * Translates the given <code>Entity</code> to it's <code>T</code> equivalent
     * @param entity        - <code>Entity</code> to translate
     * @param klass         - Return type of the result
     *
     * @return              - Instance of <code>T</code> that corresponds to <code>Entity</code>
     * @throws CentaurException
     */
    public <T> T fromEntity(final Entity entity, Class<?> klass) throws CentaurException;
}
