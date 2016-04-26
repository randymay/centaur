package com.blaazinsoftware.centaur.data;

import com.google.appengine.api.datastore.Cursor;

/**
 * @author Randy May
 *         Date: 15-09-30
 */
public abstract class QueryOptions {
    private Cursor cursor = null;

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }
}
