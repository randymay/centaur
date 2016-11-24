package com.blaazinsoftware.centaur.data.service;

import com.blaazinsoftware.centaur.data.entity.AbstractEntity;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class EntityWithBooleanFields extends AbstractEntity {
    private boolean booleanValue1;
    private Boolean booleanValue2;

    public boolean isBooleanValue1() {
        return booleanValue1;
    }

    public void setBooleanValue1(boolean booleanValue1) {
        this.booleanValue1 = booleanValue1;
    }

    public Boolean getBooleanValue2() {
        return booleanValue2;
    }

    public void setBooleanValue2(Boolean booleanValue2) {
        this.booleanValue2 = booleanValue2;
    }
}
