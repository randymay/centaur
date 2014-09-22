package org.blaazin.centaur.service;

import org.blaazin.centaur.data.dto.AbstractUserOwnedEntity;

public class UserEntity extends AbstractUserOwnedEntity {

    private String firstName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
