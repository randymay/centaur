package com.blaazin.gae.data.dto;

public abstract class AbstractDescribedNamedEntity extends AbstractIDEntity {

    private String shortDescription;
    private String longDescription;

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
}
