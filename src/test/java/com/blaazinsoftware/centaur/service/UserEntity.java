package com.blaazinsoftware.centaur.service;

import com.blaazinsoftware.centaur.data.dto.AbstractUserOwnedEntity;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class UserEntity extends AbstractUserOwnedEntity {

    private String firstName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
