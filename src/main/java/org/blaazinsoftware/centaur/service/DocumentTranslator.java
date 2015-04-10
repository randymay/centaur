package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.search.Document;
import org.blaazinsoftware.centaur.CentaurException;

/**
 * @author Randy May
 */
public interface DocumentTranslator {

    /**
     * Translates the given <code>T</code> to <code>Document</code>
     *
     * @param object        - Object to translate
     *
     * @return              - Document representation
     *
     * @throws org.blaazinsoftware.centaur.CentaurException
     */
    public <T> Document toDocument(final T object) throws CentaurException;

    /**
     * Translates the given <code>Document</code> to it's <code>T</code> equivalent
     * @param document      - <code>Document</code> to translate
     * @param klass         - Return type of the result
     *
     * @return              - Instance of <code>T</code> that corresponds to <code>Document</code>
     * @throws org.blaazinsoftware.centaur.CentaurException
     */
    public <T> T fromDocument(final Document document, Class<T> klass) throws CentaurException;
}
