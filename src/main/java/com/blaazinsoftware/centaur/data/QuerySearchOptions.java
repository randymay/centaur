package com.blaazinsoftware.centaur.data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Randy May
 *         Date: 15-09-30
 */
public class QuerySearchOptions<T> extends QueryOptions {
    private int offset = 0;
    private int limit = 0;
    private String orderByField;
    private boolean descending = true;
    private Map<String, Object> filters = new LinkedHashMap<>();
    private Class<T> returnType;

    public QuerySearchOptions(Class<T> returnType) {
        this.returnType(returnType);
    }

    public int getOffset() {
        return offset;
    }

    public QuerySearchOptions offset(int offset) {
        this.offset = offset;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public QuerySearchOptions limit(int limit) {
        this.limit = limit;
        return this;
    }

    public String getOrderByField() {
        return orderByField;
    }

    public QuerySearchOptions orderByField(String orderByField) {
        this.orderByField = orderByField;
        return this;
    }

    public boolean isDescending() {
        return descending;
    }

    public QuerySearchOptions descending(boolean descending) {
        this.descending = descending;
        return this;
    }

    public Map<String, Object> getFilter() {
        return filters;
    }

    public QuerySearchOptions filter(Map<String, Object> filter) {
        this.filters = filter;
        return this;
    }

    public QuerySearchOptions filter(String propertyName, Object value) {
        this.filters.put(propertyName, value);
        return this;
    }

    public Class<T> getReturnType() {
        return returnType;
    }

    public QuerySearchOptions returnType(Class<T> returnType) {
        this.returnType = returnType;
        return this;
    }
}
