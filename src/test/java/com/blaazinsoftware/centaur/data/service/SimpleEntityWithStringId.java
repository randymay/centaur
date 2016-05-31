package com.blaazinsoftware.centaur.data.service;

import com.blaazinsoftware.centaur.data.entity.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import javax.validation.constraints.NotNull;

@Entity
public class SimpleEntityWithStringId {
    @Id
    @JsonIgnore
    private String id;
    @NotNull
    @Index
    private String shortDescription;
    private String longDescription;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retrieves the Short Description
     *
     * @return - <code>String</code> representing the Short Description
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * Sets the Short Description
     *
     * @param shortDescription - <code>String</code> short description
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * Retrieves the Long Description
     *
     * @return - <code>String</code> representing the Long Description
     */
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * Sets the Long Description
     *
     * @param longDescription - <code>String</code> long description
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    /**
     * Returns the value of the Long Description in String form
     *
     * @return Long Description in <code>String</code> form
     */
    public String getLongDescriptionValue() {
        return this.getLongDescription() != null ? this.getLongDescription() : null;
    }

    @Override
    public String toString() {
        if (null == id) {
            return "null";
        }
        return "ID: " + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractEntity) {
            AbstractEntity entity = (AbstractEntity)obj;
            return !(id == null || entity.getId() == null) && (id.equals(entity.getId()));
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return 0;
        }
        return id.hashCode();
    }
}
