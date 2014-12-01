package org.blaazinsoftware.centaur.data.dto;

/**
 * Convenience class for using Centaur.  This class contains fields for Keys, Kind, and name which are needed
 * to store and retrieve data in Google App Engine, as well as fields for short and long descriptions.
 */
public abstract class AbstractDescribedNamedEntity extends AbstractIDEntity {

    private String shortDescription;
    private String longDescription;

    /**
     * Retrieves the Short Description
     *
     * @return          - <code>String</code> representing the Short Description
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * Sets the Short Description
     *
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * Retrieves the Long Description
     *
     * @return          - <code>String</code> representing the Long Description
     */
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * Sets the Long Description
     *
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
}
