package com.blaazin.gae.service;

import com.blaazin.gae.BlaazinGAEException;
import com.blaazin.gae.data.dto.BlaazinEntity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;

import java.util.List;
import java.util.Map;

public interface GeneralService {

    public <T extends BlaazinEntity> Key save(T object) throws BlaazinGAEException;

    public <T extends BlaazinEntity> Key save(T object, Transaction transaction) throws BlaazinGAEException;

    public <T extends BlaazinEntity, X extends BlaazinEntity> Key saveChild(X parent, T object) throws BlaazinGAEException;

    public <T extends BlaazinEntity, X extends BlaazinEntity> Key saveChild(X parent, T object, Transaction transaction) throws BlaazinGAEException;

    public <T extends BlaazinEntity> T getObject(Key key, Class<T> klass) throws BlaazinGAEException;

    public <T extends BlaazinEntity> T getObject(String kind, String name, Class<T> klass) throws BlaazinGAEException;

    public <T extends BlaazinEntity> T getObject(String kind, long id, Class<T> klass) throws BlaazinGAEException;

    public <T extends BlaazinEntity> T getObject(String name, Class<T> klass) throws BlaazinGAEException;

    public <T extends BlaazinEntity> T getObject(String propertyName, Object value, Class<T> klass) throws BlaazinGAEException;

    public <T extends BlaazinEntity> T getObjectByUserId(String userId, Class<T> klass) throws BlaazinGAEException;

    public <T extends BlaazinEntity> void deleteObject(T object) throws BlaazinGAEException;

    public <T extends BlaazinEntity> T getObjectByUserId(String kind, String userId, Class<T> klass) throws BlaazinGAEException;

    public <T extends BlaazinEntity> Key createKey(T object) throws BlaazinGAEException;

    public <T extends BlaazinEntity, X extends BlaazinEntity> Key createKey(X parent, T object) throws BlaazinGAEException;

    public <T extends BlaazinEntity, X extends BlaazinEntity> List<T> getChildren(String kind, X parent, Class<T> klass) throws BlaazinGAEException;

    public <T extends BlaazinEntity> T getObject(String kind, String property, Object value, Class<T> klass) throws BlaazinGAEException;

    public <T extends BlaazinEntity> List<T> getObjects(String kind, Class<T> klass) throws BlaazinGAEException;

    public <T extends BlaazinEntity> List<T> getObjects(String kind, String propertyName, Object value, Class<T> klass) throws BlaazinGAEException;

    public <T extends BlaazinEntity> List<T> getObjects(String kind, Map<String, Object> keyValues, Class<T> klass) throws BlaazinGAEException;

    public Transaction beginTransaction();

    public void rollback(Transaction transaction);

    public void commit(Transaction transaction);
}
