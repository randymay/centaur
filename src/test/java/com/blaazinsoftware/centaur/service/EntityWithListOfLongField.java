package com.blaazinsoftware.centaur.service;

import com.blaazinsoftware.centaur.data.dto.AbstractIDEntity;
import com.googlecode.objectify.annotation.Entity;

import java.util.List;

@Entity
public class EntityWithListOfLongField extends AbstractIDEntity {
    private List<Long> userIds;

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}
