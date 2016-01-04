package com.blaazinsoftware.centaur.service;

import com.blaazinsoftware.centaur.data.dto.AbstractIDEntity;
import com.googlecode.objectify.annotation.Entity;

import java.util.List;

@Entity
public class EntityWithListOfIntegerField extends AbstractIDEntity {
    private List<Integer> userIds;

    public List<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }
}
