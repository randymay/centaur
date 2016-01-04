package com.blaazinsoftware.centaur.service;

import com.blaazinsoftware.centaur.data.dto.AbstractEntity;
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
