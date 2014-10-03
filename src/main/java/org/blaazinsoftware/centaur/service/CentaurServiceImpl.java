package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import org.blaazinsoftware.centaur.CentaurException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Randy May <a href="www.blaazinsoftware.com">Blaazin Software Consulting, Inc.</a>
 */
public class CentaurServiceImpl implements CentaurService {

    private EntityTranslator entityTranslator;
    private CentaurDAO dao;

    @Override
    public <T> Key save(T object) throws CentaurException {
        return save(object, null);
    }

    @Override
    public <T> Key save(T object, Transaction transaction) throws CentaurException {
        Key key = CentaurServiceUtils.getKey(object);
        if (null == key) {
            CentaurServiceUtils.initKey(object);
        }
        key = dao.save(transaction, entityTranslator.toEntity(object));
        if (null == CentaurServiceUtils.getKey(object)) {
            CentaurServiceUtils.setKey(object, key);
        }

        return key;
    }

    @Override
    public <T, X> Key saveChild(X parent, T object) throws CentaurException {
        return this.saveChild(parent, object, null);
    }

    @Override
    public <T, X> Key saveChild(X parent, T object, Transaction transaction) throws CentaurException {
        if (null == parent || null == CentaurServiceUtils.getKey(parent)) {
            throw new CentaurException("Parent is null, or no Key field found");
        }

        Key key;
        if (null == CentaurServiceUtils.getKey(object)) {
            key = CentaurServiceUtils.createKey(parent, object);
            CentaurServiceUtils.setKey(object, key);
            if (null == key) {
                key = dao.save(transaction, entityTranslator.toEntity(object, CentaurServiceUtils.getKey(parent)));
            } else {
                CentaurServiceUtils.setKey(object, key);
                key = dao.save(transaction, entityTranslator.toEntity(object));
            }
        } else {
            key = dao.save(transaction, entityTranslator.toEntity(object));
        }
        if (null == CentaurServiceUtils.getKey(object)) {
            CentaurServiceUtils.setKey(object, key);
        }

        return key;
    }

    @Override
    public <T> T getObject(Key key, Class<T> klass) throws CentaurException {
        return entityTranslator.fromEntity(dao.getByKey(key), klass);
    }

    @Override
    public <T> T getObject(String kind, String name, Class<T> klass) throws CentaurException {
        Key key = KeyFactory.createKey(kind, name);

        return entityTranslator.fromEntity(dao.getByKey(key), klass);
    }

    @Override
    public <T> T getObject(String kind, long id, Class<T> klass) throws CentaurException {
        Key key = KeyFactory.createKey(kind, id);

        return entityTranslator.fromEntity(dao.getByKey(key), klass);
    }

    @Override
    public <T> T getObject(String name, Class<T> klass) throws CentaurException {
        Key key = KeyFactory.createKey(klass.getSimpleName(), name);

        return entityTranslator.fromEntity(dao.getByKey(key), klass);
    }

    @Override
    public <T> T getObject(String propertyName, Object value, Class<T> klass) throws CentaurException {
        return this.getObject(klass.getSimpleName(), propertyName, value, klass);
    }

    @Override
    public <T> T getObjectByUserId(String userId, Class<T> klass) throws CentaurException {
        return getObject(klass.getSimpleName(), "userId", userId, klass);
    }

    @Override
    public <T> T getObjectByUserId(String kind, String userId, Class<T> klass) throws CentaurException {
        return getObject(kind, "userId", userId, klass);
    }

    @Override
    public <T> T getObject(String kind, String propertyName, Object value, Class<T> klass) throws CentaurException {
        Entity entity = dao.getSingleEntityByPropertyValue(kind, propertyName, value);
        if (entity == null) {
            return null;
        }

        return entityTranslator.fromEntity(entity, klass);
    }

    @Override
    public <T> void deleteObject(T object) throws CentaurException {
        this.deleteObject(object, null);
    }

    @Override
    public <T> void deleteObject(T object, Transaction transaction) throws CentaurException {
        dao.delete(transaction, entityTranslator.toEntity(object));
    }

    @Override
    public <T, X> List<T> getChildren(String kind, X parent, Class<T> klass) throws CentaurException {
        Entity parentEntity = entityTranslator.toEntity(parent);
        List<Entity> entities = dao.getChildren(kind, parentEntity);

        List<T> results = new ArrayList<>();
        if (null == entities) {
            return null;
        }

        for (Entity entity : entities) {
            T object = entityTranslator.fromEntity(entity, klass);
            results.add(object);
        }

        return results;
    }

    @Override
    public <T> List<T> getObjects(String kind, Class<T> klass) throws CentaurException {
        List<T> entityList = new ArrayList<>();
        List<Entity> entities = dao.getEntitiesByKind(kind);
        if (entities == null) {
            return null;
        }

        for (Entity entity : entities) {
            T object = entityTranslator.fromEntity(entity, klass);
            entityList.add(object);
        }

        return entityList;
    }

    @Override
    public <T> List<T> getObjects(String kind, String propertyName, Object value, Class<T> klass) throws CentaurException {
        List<Entity> entities = dao.getEntitiesByPropertyValue(kind, propertyName, value);
        return getObjectListFromEntities(klass, entities);
    }

    private <T> List<T> getObjectListFromEntities(Class<T> klass, List<Entity> entities) throws CentaurException {
        if (entities == null) {
            return null;
        }

        List<T> objectList = new ArrayList<>();
        for (Entity entity : entities) {
            T object = entityTranslator.fromEntity(entity, klass);
            objectList.add(object);
        }

        return objectList;
    }

    @Override
    public <T> List<T> getObjects(String kind, Map<String, Object> keyValues, Class<T> klass) throws CentaurException {
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
    protected void setEntityTranslator(EntityTranslator entityTranslator) {
        this.entityTranslator = entityTranslator;
    }

}
