package org.blaazinsoftware.centaur.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.appengine.api.datastore.Key;
import org.blaazinsoftware.centaur.annotation.AppEngineKey;
import org.blaazinsoftware.centaur.annotation.AppEngineKind;
import org.blaazinsoftware.centaur.annotation.AppEngineName;

import java.io.Serializable;

public abstract class AbstractIDEntity implements CentaurEntity, Serializable {

    @AppEngineKey
    @JsonIgnore
    private Key appEngineKey;

    @AppEngineKind
    @JsonIgnore
    private String kind;

    @JsonIgnore
    @AppEngineName
    private String name;

    public AbstractIDEntity() {
        this.kind = this.getClass().getSimpleName();
    }

    public Key getAppEngineKey() {
        return appEngineKey;
    }

    public void setAppEngineKey(Key appEngineKey) {
        this.appEngineKey = appEngineKey;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
