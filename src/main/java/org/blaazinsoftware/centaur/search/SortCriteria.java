package org.blaazinsoftware.centaur.search;

/**
 * Immutable class representing the sort criteria to be used by <code>CentaurDAO</code>.
 */
public class SortCriteria {
    private String propertyName;
    private boolean ascending = true;

    public SortCriteria(String propertyName, boolean ascending) {
        this.propertyName = propertyName;
        this.ascending = ascending;
    }

    /**
     * Retrieves the property name to use for sorting
     *
     * @return              - Property Name
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Retrieves the ascending values used for sorting
     *
     * @return              - Ascending
     */
    public boolean isAscending() {
        return ascending;
    }
}
