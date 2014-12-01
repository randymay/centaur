package org.blaazinsoftware.centaur.data.dto;

/**
 * Convenience class for using Centaur.  This class contains fields for Keys, Kind, and name which are needed
 * to store and retrieve data in Google App Engine as well as a field for User Id.
 */
public class AbstractUserOwnedEntity extends AbstractIDEntity {

    private String userId;

    /**
     * Retrieves the User Id
     *
     * @return          - <code>String</code> representing the User Id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the User Id
     *
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
