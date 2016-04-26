package com.blaazinsoftware.centaur.search.dto;

import com.blaazinsoftware.centaur.data.entity.AbstractEntity;
import com.google.appengine.api.datastore.Text;

/**
 * @author Randy May
 *         Date: 2016-01-10
 */
public class AbstractNamedSearchEntity extends AbstractEntity {
    private Text name;

    public Text getName() {
        return name;
    }

    public void setName(Text name) {
        this.name = name;
    }
}
