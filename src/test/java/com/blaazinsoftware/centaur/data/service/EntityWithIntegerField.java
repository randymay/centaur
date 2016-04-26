package com.blaazinsoftware.centaur.data.service;

import com.blaazinsoftware.centaur.data.entity.AbstractEntity;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

@Entity
public class EntityWithIntegerField extends AbstractEntity {
    @Index
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
