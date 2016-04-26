package com.blaazinsoftware.centaur.search.service;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

/**
 * @author Randy May
 *         Date: 2016-01-10
 */
public class DefaultSearchServiceImpl implements SearchService {
    private Index index;

    protected DefaultSearchServiceImpl(Index index) {
        this.index = index;
    }

    public void indexDocument(String indexName, Document document) {
        index.put(document);
    }

    public Results<ScoredDocument> search(String queryString) {
        return index.search(queryString);
    }
}
