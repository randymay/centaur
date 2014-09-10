package org.blaazin.centaur.service.impl;

import org.blaazin.centaur.data.dto.AbstractIDEntity;

public class EntityWithIntegerField extends AbstractIDEntity {
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
