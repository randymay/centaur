package com.blaazinsoftware.centaur.data.dto;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Index;

/**
 * Convenience class for using Centaur.  This class contains fields for Keys, Kind, and name which are needed
 * to store and retrieve data in Google App Engine, as well as fields for short and long descriptions.
 */
public abstract class AbstractDescribedNamedEntity extends AbstractIDEntity {

    @Index
    private String shortDescription;
    private Text longDescription;

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
    public Text getLongDescription() {
        return longDescription;
    }

    /**
     * Sets the Long Description
     *
     */
    public void setLongDescription(Text longDescription) {
        this.longDescription = longDescription;
    }

    /**
     * Returns the value of the Long Description in String form
     *
     * @return          Long Description in <code>String</code> form
     */
    public String getLongDescriptionValue() {
        return this.getLongDescription() != null ? this.getLongDescription().getValue() : null;
    }
}
