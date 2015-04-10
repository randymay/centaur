package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.OperationResult;
import com.google.appengine.api.search.checkers.Preconditions;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Randy May
 *         Date: 15-04-09
 */
public class SearchResults <T> implements Serializable {

    private final OperationResult operationResult;
    private final Collection<T> results;
    private final long numberFound;
    private final int numberReturned;
    private final Cursor cursor;

    protected SearchResults(OperationResult operationResult, Collection<T> results, long numberFound, int numberReturned, Cursor cursor) {
        this.operationResult = (OperationResult) Preconditions.checkNotNull(operationResult, "operation result cannot be null");
        this.results = Collections.unmodifiableCollection((Collection) Preconditions.checkNotNull(results, "search results cannot be null"));
        this.numberFound = numberFound;
        this.numberReturned = numberReturned;
        this.cursor = cursor;
    }

    public OperationResult getOperationResult() {
        return operationResult;
    }

    public Collection<T> getResults() {
        return results;
    }

    public long getNumberFound() {
        return numberFound;
    }

    public int getNumberReturned() {
        return numberReturned;
    }

    public Cursor getCursor() {
        return cursor;
    }
}
