package com.blaazinsoftware.centaur.data.service;

import com.blaazinsoftware.centaur.data.entity.AbstractEntity;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by randymay on 2016-11-25.
 */
@Entity
public class EntityWithRef extends AbstractEntity {
    @Index
    private Ref<UserEntity> userEntity;

    public UserEntity getUserEntity() {
        return userEntity.get();
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = Ref.create(userEntity);
    }
}
