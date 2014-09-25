package org.blaazinsoftware.centaur.service;

import org.blaazinsoftware.centaur.data.dto.AbstractIDEntity;

public class EntityWithStringAndIntegerField extends AbstractIDEntity {
    private Integer intValue;
    private String stringValue;

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
