package org.blaazinsoftware.centaur.query;

import com.google.appengine.api.datastore.Query;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Randy May
 *         Date: 15-05-01
 */
public class DataOptions {
    private Map<String, Query.SortDirection> sortOptions = new LinkedHashMap<>();

    public void addSortOption(String fieldName, boolean ascending) {
        sortOptions.put(fieldName, ascending ? Query.SortDirection.ASCENDING : Query.SortDirection.DESCENDING);
    }

    public Map<String, Query.SortDirection> getSortOptions() {
        return sortOptions;
    }
}
