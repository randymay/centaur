package org.blaazinsoftware.centaur.service;

import org.blaazinsoftware.centaur.data.dto.AbstractIDEntity;

import java.util.Date;

public class EntityWithDateField extends AbstractIDEntity {
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
