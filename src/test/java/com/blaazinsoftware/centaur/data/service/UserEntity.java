package com.blaazinsoftware.centaur.data.service;

import com.blaazinsoftware.centaur.data.entity.AbstractEntity;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class UserEntity extends AbstractEntity {

    private String firstName;
    private String userId;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
