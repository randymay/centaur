package org.blaazin.centaur.data.dto;

public class AbstractUserOwnedEntity extends AbstractIDEntity {

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
