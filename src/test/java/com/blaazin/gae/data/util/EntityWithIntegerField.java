package com.blaazin.gae.data.util;

import com.blaazin.gae.data.dto.AbstractIDEntity;

public class EntityWithIntegerField extends AbstractIDEntity {
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
