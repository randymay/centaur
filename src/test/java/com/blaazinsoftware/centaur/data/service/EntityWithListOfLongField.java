package com.blaazinsoftware.centaur.data.service;

import com.blaazinsoftware.centaur.data.entity.AbstractEntity;
import com.googlecode.objectify.annotation.Entity;

import java.util.List;

@Entity
public class EntityWithListOfLongField extends AbstractEntity {
    private List<Long> userIds;

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}
