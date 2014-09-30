package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import org.apache.commons.lang3.StringUtils;
import org.blaazinsoftware.centaur.CentaurException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CentaurServiceImpl implements CentaurService {

    private CentaurDAO dao;
    private CentaurServiceConfig config;

    @Override
    public <T> Key save(T object) throws CentaurException {
        return save(object, null);
    }

    @Override
    public <T> Key save(T object, Transaction transaction) throws CentaurException {
        setNamespace();

        Key key = CentaurServiceUtils.getKey(object);
        if (null == key) {
            CentaurServiceUtils.initKey(object);
        }
        key = dao.save(transaction, new EntityTranslator().toEntity(object));
        if (null == CentaurServiceUtils.getKey(object)) {
            CentaurServiceUtils.setKey(object, key);
        }

        return key;
    }

    @Override
    public <T, X> Key saveChild(X parent, T object) throws CentaurException {
        setNamespace();
        Key childKey = this.saveChild(parent, object, null);
        if (null == CentaurServiceUtils.getKey(object)) {
            CentaurServiceUtils.setKey(object, childKey);
        }

        return childKey;
    }

    @Override
    public <T, X> Key saveChild(X parent, T object, Transaction transaction) throws CentaurException {
        setNamespace();
        if (null == parent || null == CentaurServiceUtils.getKey(parent)) {
            throw new CentaurException("Parent is null, or no Key field found");
        }

        if (null == CentaurServiceUtils.getKey(object)) {
            Key key = CentaurServiceUtils.createKey(parent, object);
            CentaurServiceUtils.setKey(object, key);
            if (null == key) {
                return dao.save(transaction, new EntityTranslator().toEntity(object, CentaurServiceUtils.getKey(parent)));
            }
        }
        return dao.save(transaction, new EntityTranslator().toEntity(object));
    }

    @Override
    public <T> T getObject(Key key, Class<T> klass) throws CentaurException {
        setNamespace();
        return new EntityTranslator().fromEntity(dao.getByKey(key), klass);
    }

    @Override
    public <T> T getObject(String kind, String name, Class<T> klass) throws CentaurException {
        setNamespace();
        Key key = KeyFactory.createKey(kind, name);

        return new EntityTranslator().fromEntity(dao.getByKey(key), klass);
    }

    @Override
    public <T> T getObject(String kind, long id, Class<T> klass) throws CentaurException {
        setNamespace();
        Key key = KeyFactory.createKey(kind, id);

        return new EntityTranslator().fromEntity(dao.getByKey(key), klass);
    }

    @Override
    public <T> T getObject(String name, Class<T> klass) throws CentaurException {
        setNamespace();
        Key key = KeyFactory.createKey(klass.getSimpleName(), name);

        return new EntityTranslator().fromEntity(dao.getByKey(key), klass);
    }

    @Override
    public <T> T getObject(String propertyName, Object value, Class<T> klass) throws CentaurException {
        setNamespace();
        return this.getObject(klass.getSimpleName(), propertyName, value, klass);
    }

    @Override
    public <T> T getObjectByUserId(String userId, Class<T> klass) throws CentaurException {
        setNamespace();
        return getObject(klass.getSimpleName(), "userId", userId, klass);
    }

    @Override
    public <T> T getObjectByUserId(String kind, String userId, Class<T> klass) throws CentaurException {
        setNamespace();
        return getObject(kind, "userId", userId, klass);
    }

    @Override
    public <T> T getObject(String kind, String propertyName, Object value, Class<T> klass) throws CentaurException {
        setNamespace();
        Entity entity = dao.getSingleEntityByPropertyValue(kind, propertyName, value);
        if (entity == null) {
            return null;
        }

        return new EntityTranslator().fromEntity(entity, klass);
    }

    @Override
    public <T> void deleteObject(T object) throws CentaurException {
        setNamespace();
        this.deleteObject(object, null);
    }

    @Override
    public <T> void deleteObject(T object, Transaction transaction) throws CentaurException {
        setNamespace();
        dao.delete(transaction, new EntityTranslator().toEntity(object));
    }

    @Override
    public <T, X> List<T> getChildren(String kind, X parent, Class<T> klass) throws CentaurException {
        setNamespace();
        Entity parentEntity = new EntityTranslator().toEntity(parent);
        List<Entity> entities = dao.getChildren(kind, parentEntity);

        List<T> results = new ArrayList<>();
        if (null == entities) {
            return null;
        }

        for (Entity entity : entities) {
            T object = new EntityTranslator().fromEntity(entity, klass);
            results.add(object);
        }

        return results;
    }

    @Override
    public <T> List<T> getObjects(String kind, Class<T> klass) throws CentaurException {
        setNamespace();
        List<T> entityList = new ArrayList<>();
        List<Entity> entities = dao.getEntitiesByKind(kind);
        if (entities == null) {
            return null;
        }

        for (Entity entity : entities) {
            T object = new EntityTranslator().fromEntity(entity, klass);
            entityList.add(object);
        }

        return entityList;
    }

    @Override
    public <T> List<T> getObjects(String kind, String propertyName, Object value, Class<T> klass) throws CentaurException {
        setNamespace();
        List<Entity> entities = dao.getEntitiesByPropertyValue(kind, propertyName, value);
        return getObjectListFromEntities(klass, entities);
    }

    private <T> List<T> getObjectListFromEntities(Class<T> klass, List<Entity> entities) throws CentaurException {
        setNamespace();
        if (entities == null) {
            return null;
        }

        List<T> objectList = new ArrayList<>();
        for (Entity entity : entities) {
            T object = new EntityTranslator().fromEntity(entity, klass);
            objectList.add(object);
        }

        return objectList;
    }

    @Override
    public <T> List<T> getObjects(String kind, Map<String, Object> keyValues, Class<T> klass) throws CentaurException {
        setNamespace();
        List<Entity> entities = dao.getEntitiesByPropertyValues(kind, keyValues);
        return getObjectListFromEntities(klass, entities);
    }

    public Transaction beginTransaction() {
        return dao.beginTransaction();
    }

    public void rollback(Transaction transaction) {
        dao.rollbackTransaction(transaction);
    }

    public void commit(Transaction transaction) {
        dao.commitTransaction(transaction);
    }

    protected void setDao(CentaurDAO dao) {
        this.dao = dao;
    }

    private void setNamespace() {
        if (null != config && !StringUtils.isEmpty(config.getNamespace())) {
            NamespaceManager.set(config.getNamespace());
        }
    }

    public CentaurServiceConfig getConfig() {
        return config;
    }

    protected void setConfig(CentaurServiceConfig config) {
        this.config = config;
    }
}
