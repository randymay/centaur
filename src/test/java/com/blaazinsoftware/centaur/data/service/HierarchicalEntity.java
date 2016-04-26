package com.blaazinsoftware.centaur.data.service;

import com.blaazinsoftware.centaur.data.RefList;
import com.blaazinsoftware.centaur.data.entity.AbstractDescribedNamedEntity;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class HierarchicalEntity extends AbstractDescribedNamedEntity {
    private RefList<SimpleEntity> childEntities = new RefList<>();

    public RefList<SimpleEntity> getChildEntities() {
        return childEntities;
    }

    public void setChildEntities(RefList<SimpleEntity> childEntities) {
        this.childEntities = childEntities;
    }
}
