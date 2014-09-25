package org.blaazinsoftware.centaur.service;

import org.blaazinsoftware.centaur.data.dto.AbstractUserOwnedEntity;

public class UserEntity extends AbstractUserOwnedEntity {

    private String firstName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
