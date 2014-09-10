package org.blaazin.centaur.service.impl;

import org.blaazin.centaur.data.dto.AbstractIDEntity;

public class EntityWithBooleanFields extends AbstractIDEntity {
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
