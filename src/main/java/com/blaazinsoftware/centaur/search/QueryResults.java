package com.blaazinsoftware.centaur.search;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Randy May
 *         Date: 15-09-30
 */
public class QueryResults<T> extends QueryOptions {
    private List<T> results = new ArrayList<>();
    private int countReturned = 0;
    private int countFound = 0;

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public int getCountReturned() {
        return countReturned;
    }

    public void setCountReturned(int countReturned) {
        this.countReturned = countReturned;
    }

    public int getCountFound() {
        return countFound;
    }

    public void setCountFound(int countFound) {
        this.countFound = countFound;
    }
}
