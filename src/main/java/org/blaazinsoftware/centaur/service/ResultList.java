package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Index;
import com.google.appengine.api.datastore.QueryResultList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Randy May
 *         Date: 14-10-30
 */
public class ResultList <T> extends ArrayList<T> implements QueryResultList<T> {
    private List<Index> indexList;
    private Cursor cursor;

    public List<Index> getIndexList() {
        return indexList;
    }

    protected void setIndexList(List<Index> indexList) {
        this.indexList = indexList;
    }

    public Cursor getCursor() {
        return cursor;
    }

    protected void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }
}
