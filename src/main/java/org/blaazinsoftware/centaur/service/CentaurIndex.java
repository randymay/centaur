package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import org.blaazinsoftware.centaur.CentaurException;

/**
 * @author Randy May
 *         Date: 15-01-09
 */
public interface CentaurIndex {
    public <T> void indexDocument(Document document, Class<T> expectedReturnType) throws CentaurException;
    public void indexDocument(Document document, String indexName) throws CentaurException;
    public <T> boolean existsInIndex(String documentId, String indexName) throws CentaurException;
    public <T> void updateDocumentInIndex(Document document, Class<T> expectedReturnType) throws CentaurException;
    public <T> void removeDocumentFromIndex(Document document, Class<T> expectedReturnType) throws CentaurException;
    public <T> void removeDocumentFromIndex(String documentId, Class<T> expectedReturnType) throws CentaurException;
    public <T> Results<ScoredDocument> search(Class<T> expectedReturnType, Query query) throws CentaurException;
}
