package com.blaazinsoftware.centaur.search.service;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

/**
 * @author Randy May
 *         Date: 2016-01-10
 */
public interface SearchService {
    void indexDocument(String indexName, Document document);
    Results<ScoredDocument> search(String queryString);
}
