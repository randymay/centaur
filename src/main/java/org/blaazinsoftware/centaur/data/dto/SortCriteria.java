package org.blaazinsoftware.centaur.data.dto;

public class SortCriteria {
    private String propertyName;
    private boolean ascending = true;

    public SortCriteria() {

    }

    public SortCriteria(String propertyName, boolean ascending) {
        this.propertyName = propertyName;
        this.ascending = ascending;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }
}
