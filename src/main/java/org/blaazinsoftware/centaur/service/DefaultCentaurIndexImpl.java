package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.search.*;
import org.blaazinsoftware.centaur.CentaurException;

/**
 * @author Randy May
 *         Date: 15-04-08
 */
public class DefaultCentaurIndexImpl implements CentaurIndex {
    @Override
    public <T> void indexDocument(Document document, Class<T> expectedReturnType) throws CentaurException {
        final String indexName = expectedReturnType.getCanonicalName();
        indexDocument(document, indexName);
    }

    @Override
    public void indexDocument(Document document, String indexName) throws CentaurException {
        Index index = getIndex(indexName);

        try {
            index.put(document);
        } catch (PutException e) {
            throw new CentaurException(e);
        }
    }

    private Index getIndex(String indexName) {
        IndexSpec indexSpec = IndexSpec.newBuilder().setName(indexName).build();
        return SearchServiceFactory.getSearchService().getIndex(indexSpec);
    }

    @Override
    public <T> boolean existsInIndex(String documentId, String indexName) throws CentaurException {
        return getIndex(indexName).get(documentId) != null;
    }

    @Override
    public <T> void updateDocumentInIndex(Document document, Class<T> expectedReturnType) throws CentaurException {
        indexDocument(document, expectedReturnType);
    }

    @Override
    public <T> void removeDocumentFromIndex(Document document, Class<T> expectedReturnType) throws CentaurException {
        removeDocumentFromIndex(document.getId(), expectedReturnType);
    }

    @Override
    public <T> void removeDocumentFromIndex(String documentId, Class<T> expectedReturnType) throws CentaurException {
        final String indexName = expectedReturnType.getCanonicalName();
        getIndex(indexName).delete(documentId);
    }

    @Override
    public <T> Results<ScoredDocument> search(Class<T> expectedReturnType, Query query) throws CentaurException {
        return search(expectedReturnType.getCanonicalName(), query);
    }

    protected Results<ScoredDocument> search(String indexName, Query query) throws CentaurException {
        return getIndex(indexName).search(query);
    }
}
