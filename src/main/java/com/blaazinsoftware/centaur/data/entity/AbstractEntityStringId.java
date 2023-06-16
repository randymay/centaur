package com.blaazinsoftware.centaur.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;

/**
 * Convenient base class for using Centaur.
 */
public abstract class AbstractEntityStringId implements Serializable {

    @Id
    @JsonIgnore
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        if (obj instanceof AbstractEntityStringId) {
            AbstractEntityStringId entity = (AbstractEntityStringId)obj;
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

    public String getWebSafeKey() {
        return Key.create(this).toUrlSafe();
    }
}
