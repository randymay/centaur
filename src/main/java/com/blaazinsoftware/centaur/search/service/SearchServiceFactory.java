package com.blaazinsoftware.centaur.search.service;

import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;

/**
 * @author Randy May
 *         Date: 2016-01-10
 */
public class SearchServiceFactory {
    private static final String DEFAULT_INDEX_NAME = "centaur-index-name";

    public static SearchService getInstance() {
        return SearchServiceFactory.getInstance(DEFAULT_INDEX_NAME);
    }

    public static SearchService getInstance(String indexName) {
        IndexSpec indexSpec = IndexSpec.newBuilder().setName(indexName).build();
        Index index = com.google.appengine.api.search.SearchServiceFactory.getSearchService().getIndex(indexSpec);

        return SearchServiceFactory.getInstance(index);
    }

    public static SearchService getInstance(Index index) {
        return new DefaultSearchServiceImpl(index);
    }
}
