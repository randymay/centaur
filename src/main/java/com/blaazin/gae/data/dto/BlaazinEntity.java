package com.blaazin.gae.data.dto;

import com.google.appengine.api.datastore.Key;

public interface BlaazinEntity {
    public Key getAppEngineKey();

    public void setAppEngineKey(Key key);

    public String getKind();

    public void setKind(String kind);

    public String getName();

    public void setName(String name);
}
