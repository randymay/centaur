package org.blaazin.centaur.data.dto;

import com.google.appengine.api.datastore.Key;

import java.util.HashMap;

public final class MapEntity extends HashMap<String, Object> implements CentaurEntity {

    private Key key;
    private String kind;
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
