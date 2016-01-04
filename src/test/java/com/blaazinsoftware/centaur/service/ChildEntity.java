package com.blaazinsoftware.centaur.service;

import com.blaazinsoftware.centaur.data.dto.AbstractDescribedNamedEntity;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class ChildEntity extends AbstractDescribedNamedEntity {
    @Parent
    private Ref<ParentEntity> parentEntity;

    public ParentEntity getParentEntity() {
        return parentEntity.get();
    }

    public void setParentEntity(ParentEntity parentEntity) {
         this.parentEntity = Ref.create(parentEntity);
    }
}
