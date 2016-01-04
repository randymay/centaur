package com.blaazinsoftware.centaur.service;

import com.blaazinsoftware.centaur.data.dto.AbstractEntity;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

@Entity
public class EntityWithDateField extends AbstractEntity {
    @Index
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
