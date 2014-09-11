package org.blaazin.centaur.service;

import org.blaazin.centaur.data.dto.AbstractIDEntity;

import java.util.List;

public class EntityWithListOfIntegerField extends AbstractIDEntity {
    private List<Integer> userIds;

    public List<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }
}
