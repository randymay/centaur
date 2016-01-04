package com.blaazinsoftware.centaur.service;

import com.blaazinsoftware.centaur.data.dto.AbstractEntity;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

@Entity
public class EntityWithStringAndIntegerField extends AbstractEntity {
    @Index
    private Integer intValue;
    @Index
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
