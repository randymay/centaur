package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Index;
import com.google.appengine.api.datastore.QueryResultList;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the <code>QueryResultList</code> to allow for paged results
 *
 * @author Randy May
 *         Date: 14-10-30
 */
public class ResultList <T> extends ArrayList<T> implements QueryResultList<T> {
    private List<Index> indexList;
    private Cursor cursor;

    /**
     * Default Constructor
     */
    public ResultList() {

    }

    /**
     * Constructor for cloning a ResultList object
     *
     * @param cursor        - <code>Cursor</code> to use
     */
    public ResultList (Cursor cursor) {
        this.cursor = cursor;
    }

    /**
     * Get the indexes used to perform the query.
     *
     * @return              - <code>Index</code>
     */
    public List<Index> getIndexList() {
        return indexList;
    }

    /**
     * Set the List of Indexes
     *
     * @param indexList     - List of Indexes
     */
    protected void setIndexList(List<Index> indexList) {
        this.indexList = indexList;
    }

    /**
     * Gets a Cursor that points to the result immediately after the last one in this list.
     *
     * @return              - <code>Cursor</code>
     */
    public Cursor getCursor() {
        return cursor;
    }

    /**
     * Gets a Cursor that points to the result immediately after the last one in this list.
     *
     * @param cursor        - <code>Cursor</code>
     */
    protected void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }
}
