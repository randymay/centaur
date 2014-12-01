package org.blaazinsoftware.centaur.data.dto;

import com.google.appengine.api.datastore.Key;
import org.blaazinsoftware.centaur.annotation.AppEngineKey;
import org.blaazinsoftware.centaur.annotation.AppEngineKind;
import org.blaazinsoftware.centaur.annotation.AppEngineName;

import java.io.Serializable;
import java.util.HashMap;

public final class MapEntity extends HashMap<String, Object> implements Serializable {

    @AppEngineKey
    private Key key;

    @AppEngineKind
    private String kind;

    @AppEngineName
    private String name;

    public MapEntity() {
        this.kind = this.getClass().getSimpleName();
        this.name = kind;
    }

    public Key getAppEngineKey() {
        return key;
    }

    public void setAppEngineKey(Key key) {
        this.key = key;
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
