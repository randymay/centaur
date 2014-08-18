package com.blaazin.gae.service;

import com.blaazin.gae.BlaazinGAEException;
import com.blaazin.gae.data.dto.BlaazinEntity;
import com.google.appengine.api.datastore.Key;

import java.util.List;
import java.util.Map;

public interface GeneralService {

    public <T extends BlaazinEntity> void save(T object) throws BlaazinGAEException;

    @Deprecated
    public <T extends BlaazinEntity> void create(T object) throws BlaazinGAEException;

    public <T extends BlaazinEntity, X extends BlaazinEntity> void create(X parent, T object) throws BlaazinGAEException;

    public <T extends BlaazinEntity> T getByKey(Key key, Class<T> klass) throws BlaazinGAEException;

    @Deprecated
    public <T extends BlaazinEntity> void update(T object) throws BlaazinGAEException;

    public <T extends BlaazinEntity> void delete(T object) throws BlaazinGAEException;

    public <T extends BlaazinEntity> T getSingleObjectByUserId(String kind, String userId, Class<T> klass) throws BlaazinGAEException;

    public <T extends BlaazinEntity> Key createKey(T object) throws BlaazinGAEException;

    public <T extends BlaazinEntity, X extends BlaazinEntity> Key createKey(X parent, T object) throws BlaazinGAEException;

    public <T extends BlaazinEntity, X extends BlaazinEntity> List<T> getChildren(String kind, X parent, Class<T> klass) throws BlaazinGAEException;

    public <T extends BlaazinEntity> T getSingleObjectByPropertyValue(String kind, String property, Object value, Class<T> klass) throws BlaazinGAEException;

    public <T extends BlaazinEntity> List<T> getObjectsByKind(String kind, Class<T> klass) throws BlaazinGAEException;

    public <T extends BlaazinEntity> List<T> getObjectsByPropertyValue(String kind, String property, Object value, Class<T> klass) throws BlaazinGAEException;

    public <T extends BlaazinEntity> List<T> getObjectsByPropertyValues(String kind, Map<String, Object> keyValues, Class<T> klass) throws BlaazinGAEException;
}
