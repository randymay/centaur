package com.blaazinsoftware.centaur.search;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.OperationResult;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Randy May
 *         Date: 15-04-09
 */
public class SearchResults<T> implements Serializable {

    private final OperationResult operationResult;
    private final Collection<T> results;
    private final long numberFound;
    private final int numberReturned;
    private final Cursor cursor;

    protected SearchResults(OperationResult operationResult, Collection<T> results, long numberFound, int numberReturned, Cursor cursor) {
        if (null == operationResult) {
            throw new NullPointerException("Operation Result cannot be null");
        }
        this.operationResult = operationResult;
        if (null == results) {
            throw new NullPointerException("Results cannot be null");
        }
        this.results = results;
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
