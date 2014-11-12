package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.*;
import org.apache.commons.collections.MapUtils;
import org.blaazinsoftware.centaur.CentaurException;
import org.blaazinsoftware.centaur.data.dto.SortCriteria;

import java.util.*;

/**
 * @author Randy May <a href="www.blaazinsoftware.com">Blaazin Software Consulting, Inc.</a>
 */
public class CentaurServiceImpl implements CentaurService {

    private EntityTranslator entityTranslator;
    private CentaurDAO dao;

    @Override
    public <T> String save(T object) throws CentaurException {
        return save(object, null);
    }

    @Override
    public <T> String save(T object, Transaction transaction) throws CentaurException {
        Key key = CentaurServiceUtils.getKey(object);
        if (null == key) {
            CentaurServiceUtils.initKey(object);
        }
        key = dao.save(transaction, entityTranslator.toEntity(object));
        if (null == CentaurServiceUtils.getKey(object)) {
            CentaurServiceUtils.setKey(object, key);
        }

        return KeyFactory.keyToString(key);
    }

    @Override
    public <T, X> String saveChild(X parent, T object) throws CentaurException {
        return this.saveChild(parent, object, null);
    }

    @Override
    public <T, X> String saveChild(X parent, T object, Transaction transaction) throws CentaurException {
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

        return KeyFactory.keyToString(key);
    }

    @Override
    public <T> T getObject(String keyString, Class<T> klass) throws CentaurException {
        Key key = KeyFactory.stringToKey(keyString);
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
    public <T> T getObjectByProperty(String propertyName, Object value, Class<T> klass) throws CentaurException {
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
    public <T> Map<String, T> getByKeyStrings(List<String> keyStrings, Class<T> klass) throws CentaurException {
        List<Key> keyList = new ArrayList<>();

        for (String keyString : keyStrings) {
            keyList.add(KeyFactory.stringToKey(keyString));
        }

        return getByKeys(keyList, klass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getByKeys(List<Key> keys, Class<T> klass) throws CentaurException {
        Map<String, T> results = null;

        Map<Key, Entity> values = dao.getByKeys(keys);

        if (!MapUtils.isEmpty(values)) {
            results = new LinkedHashMap<>();
            for (Map.Entry<Key, Entity> value : values.entrySet()) {
                Key key = value.getKey();
                Entity entity = value.getValue();

                results.put(KeyFactory.keyToString(key), (T)entityTranslator.fromEntity(entity, klass));

            }
        }

        return results;
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
    public void deleteObject(String keyString) throws CentaurException {
        deleteObject(keyString, null);
    }

    @Override
    public void deleteObject(String keyString, Transaction transaction) throws CentaurException {
        Key key = KeyFactory.stringToKey(keyString);
        Entity entity = dao.getByKey(key);
        dao.delete(transaction, entity);
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
        return getObjects(kind, propertyName, value, klass, null);
    }

    @Override
    public <T> List<T> getObjects(String kind, String propertyName, Object value, Class<T> klass, String sortField) throws CentaurException {
        return getObjectsSorted(kind, propertyName, value, klass, (SortCriteria)null);
    }

    @Override
    public <T> List<T> getObjectsSorted(String kind, String propertyName, Object value, Class<T> klass, SortCriteria... sortCriteria) throws CentaurException {
        Map<String, Object> keyValues = new HashMap<>();
        keyValues.put(propertyName, value);
        return getObjectsByPropertiesSorted(kind, keyValues, klass, sortCriteria);
    }

    @Override
    public <T> List<T> getObjectsByPropertiesSorted(String kind, Map<String, Object> keyValues, Class<T> klass, SortCriteria... sortCriteria) throws CentaurException {
        List<Entity> entities = dao.getEntitiesByPropertyValuesSorted(kind, keyValues, sortCriteria);
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
        return getObjectsByPropertiesSorted(kind, keyValues, klass, (SortCriteria)null);
    }

    @Override
    public <T> List<T> getObjects(Class<T> klass) throws CentaurException {
        return getObjects(klass.getSimpleName(), klass);
    }

    @Override
    public QueryResultList<Entity> getEntitiesByFilterSorted(String kind, Query.Filter filter, List<SortCriteria> sortCriteria, FetchOptions fetchOptions) throws CentaurException {
        return dao.getEntitiesByFilterSorted(kind, filter, sortCriteria, fetchOptions);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ResultList<T> getObjectsByFilterSorted(Class klass, Query.Filter filter, List<SortCriteria> sortCriteria, FetchOptions fetchOptions) throws CentaurException {
        ResultList<T> results = new ResultList<>();

        QueryResultList<Entity> entityList = getEntitiesByFilterSorted(klass.getSimpleName(), filter, sortCriteria, fetchOptions);

        if (entityList == null) {
            return results;
        }

        results.setIndexList(entityList.getIndexList());
        results.setCursor(entityList.getCursor());

        results.addAll(getObjectListFromEntities(klass, entityList));

        return results;
    }

    @Override
    public <T> ResultList<T> getObjectsByFilter(Class klass, Query.Filter filter) throws CentaurException {
        return getObjectsByFilterSorted(klass, filter, null, null);
    }

    public Transaction beginTransaction() {
        return dao.beginTransaction();
    }

    public Transaction beginCrossGroupTransaction() {
        return dao.beginCrossGroupTransaction();
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
