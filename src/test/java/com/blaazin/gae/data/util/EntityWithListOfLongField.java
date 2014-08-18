package com.blaazin.gae.data.util;

import com.blaazin.gae.data.dto.AbstractIDEntity;

import java.util.List;

public class EntityWithListOfLongField extends AbstractIDEntity {
    private List<Long> userIds;

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}
