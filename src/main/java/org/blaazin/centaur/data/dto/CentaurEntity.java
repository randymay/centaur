package org.blaazin.centaur.data.dto;

import com.google.appengine.api.datastore.Key;

import java.io.Serializable;

public interface CentaurEntity extends Serializable {
    public Key getAppEngineKey();

    public void setAppEngineKey(Key key);

    public String getKind();

    public String getName();
}
