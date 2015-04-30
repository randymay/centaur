package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import org.apache.commons.collections.MapUtils;
import org.blaazinsoftware.centaur.CentaurException;
import org.blaazinsoftware.centaur.search.SortCriteria;

import java.util.*;

/**
 * Default Implementation of <code>CentaurService</code>
 *
 * @author Randy May
 */
public class DefaultCentaurServiceImpl implements CentaurService {

    private EntityTranslator entityTranslator;
    private DocumentTranslator documentTranslator;
    private CentaurDAO dao;
    private CentaurCache cache;
    private CentaurIndex index;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getObjectFromCacheByKey(String keyString) throws CentaurException {
        return (T) getObjectFromCache(keyString);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getObjectFromCache(Object key) throws CentaurException {
        return (T) cache.getObjectFromCache(key);
    }

    @Override
    public <T> void cacheObjectByKey(String key, T objectToCache) throws CentaurException {
        cacheObject(key, objectToCache);
    }

    @Override
    public <T> void cacheObject(Object key, T objectToCache) throws CentaurException {
        cache.cacheObject(key, objectToCache);
    }

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
        final Entity entity = entityTranslator.toEntity(object);
        key = dao.save(transaction, entity);
        if (null == CentaurServiceUtils.getKey(object)) {
            CentaurServiceUtils.setKey(object, key);
        }

        if (null != index && index.existsInIndex(CentaurServiceUtils.keyToString(key), object.getClass().getCanonicalName())) {
            Document document = documentTranslator.toDocument(object);
            index.updateDocumentInIndex(document, object.getClass());
        }

        return KeyFactory.keyToString(key);
    }

    @Override
    public <T> String saveAndIndex(T object) throws CentaurException {
        return saveAndIndex(object, null);
    }

    @Override
    public <T> String saveAndIndex(T object, Transaction transaction) throws CentaurException {
        final Entity entity = entityTranslator.toEntity(object);
        Key key = dao.save(transaction, entity);
        if (null == CentaurServiceUtils.getKey(object)) {
            CentaurServiceUtils.setKey(object, key);
        }

        String keyString = save(object, transaction);

        Document document = documentTranslator.toDocument(object);
        indexDocument(document, object.getClass());

        return keyString;
    }

    @Override
    public <T> void indexDocument(Document document, Class<T> expectedReturnType) throws CentaurException {
        indexDocument(document, expectedReturnType.getCanonicalName());
    }

    @Override
    public void indexDocument(Document document, String indexName) throws CentaurException {
        index.indexDocument(document, indexName);
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
    public <T> T getObject(String keyString, Class<T> expectedReturnType) throws CentaurException {
        Key key = KeyFactory.stringToKey(keyString);
        return entityTranslator.fromEntity(dao.getByKey(key), expectedReturnType);
    }

    @Override
    public <T> T getObject(String kind, String name, Class<T> expectedReturnType) throws CentaurException {
        Key key = KeyFactory.createKey(kind, name);

        return entityTranslator.fromEntity(dao.getByKey(key), expectedReturnType);
    }

    @Override
    public <T> T getObject(String kind, long id, Class<T> expectedReturnType) throws CentaurException {
        Key key = KeyFactory.createKey(kind, id);

        return entityTranslator.fromEntity(dao.getByKey(key), expectedReturnType);
    }

    @Override
    public <T> T findObjectByProperty(String propertyName, Object value, Class<T> expectedReturnType) throws CentaurException {
        return this.findObject(expectedReturnType.getSimpleName(), propertyName, value, expectedReturnType);
    }

    @Override
    public <T> T findObjectByUserId(String userId, Class<T> expectedReturnType) throws CentaurException {
        return findObject(expectedReturnType.getSimpleName(), "userId", userId, expectedReturnType);
    }

    @Override
    public <T> T findObjectByUserId(String kind, String userId, Class<T> expectedReturnType) throws CentaurException {
        return findObject(kind, "userId", userId, expectedReturnType);
    }

    @Override
    public <T> T findObject(String kind, String propertyName, Object value, Class<T> expectedReturnType) throws CentaurException {
        Entity entity = dao.findSingleEntityByPropertyValue(kind, propertyName, value);
        if (entity == null) {
            return null;
        }

        return entityTranslator.fromEntity(entity, expectedReturnType);
    }

    @Override
    public <T> Map<String, T> getObjectByKeyStrings(List<String> keyStrings, Class<T> expectedReturnType) throws CentaurException {
        List<Key> keyList = new ArrayList<>();

        for (String keyString : keyStrings) {
            keyList.add(KeyFactory.stringToKey(keyString));
        }

        return getObjectByKeys(keyList, expectedReturnType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getObjectByKeys(List<Key> keys, Class<T> expectedReturnType) throws CentaurException {
        Map<String, T> results = null;

        Map<Key, Entity> values = dao.getByKeys(keys);

        if (!MapUtils.isEmpty(values)) {
            results = new LinkedHashMap<>();
            for (Map.Entry<Key, Entity> value : values.entrySet()) {
                Key key = value.getKey();
                Entity entity = value.getValue();

                results.put(KeyFactory.keyToString(key), (T) entityTranslator.fromEntity(entity, expectedReturnType));

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
        final Entity entity = entityTranslator.toEntity(object);
        dao.delete(transaction, entity);

        if (null != index && index.existsInIndex(CentaurServiceUtils.keyToString(entity.getKey()), object.getClass().getCanonicalName())) {
        final Document document = documentTranslator.toDocument(object);
            index.removeDocumentFromIndex(document, object.getClass());
        }
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
    public <T, X> List<T> getAllChildren(X parent, Class<T> expectedReturnType) throws CentaurException {
        final Key key = CentaurServiceUtils.getKey(parent);
        if (null == parent || null == key) {
            throw new CentaurException("Parent is null, or no Key field found");
        }

        try {
            final T object = expectedReturnType.newInstance();
            String kind = CentaurServiceUtils.getKindValue(object);
            return getAllChildren(kind, parent, expectedReturnType);
        } catch (Exception e) {
            throw new CentaurException(e);
        }
    }

    @Override
    public <T, X> List<T> getAllChildren(String kind, X parent, Class<T> expectedReturnType) throws CentaurException {
        Entity parentEntity = entityTranslator.toEntity(parent);
        List<Entity> entities = dao.getAllChildren(kind, parentEntity);

        List<T> results = new ArrayList<>();
        if (null == entities) {
            return null;
        }

        for (Entity entity : entities) {
            T object = entityTranslator.fromEntity(entity, expectedReturnType);
            results.add(object);
        }

        return results;
    }

    @Override
    public <T> List<T> getObjects(String kind, Class<T> expectedReturnType) throws CentaurException {
        List<T> entityList = new ArrayList<>();
        List<Entity> entities = dao.getAllEntitiesByKind(kind);
        if (entities == null) {
            return null;
        }

        for (Entity entity : entities) {
            T object = entityTranslator.fromEntity(entity, expectedReturnType);
            entityList.add(object);
        }

        return entityList;
    }

    @Override
    public <T> List<T> findObjects(String kind, String propertyName, Object value, Class<T> expectedReturnType) throws CentaurException {
        return findObjects(kind, propertyName, value, expectedReturnType, null);
    }

    @Override
    public <T> List<T> findObjects(String kind, String propertyName, Object value, Class<T> expectedReturnType, String sortField) throws CentaurException {
        return findObjectsSorted(kind, propertyName, value, expectedReturnType, (SortCriteria) null);
    }

    @Override
    public <T> List<T> findObjectsSorted(String kind, String propertyName, Object value, Class<T> expectedReturnType, SortCriteria... sortCriteria) throws CentaurException {
        Map<String, Object> keyValues = new HashMap<>();
        keyValues.put(propertyName, value);
        return findObjectsByPropertiesSorted(kind, keyValues, expectedReturnType, sortCriteria);
    }

    @Override
    public <T> List<T> findObjectsByPropertiesSorted(String kind, Map<String, Object> keyValues, Class<T> expectedReturnType, SortCriteria... sortCriteria) throws CentaurException {
        List<Entity> entities = dao.findEntitiesByPropertyValuesSorted(kind, keyValues, sortCriteria);
        return getObjectListFromEntities(expectedReturnType, entities);
    }

    private <T> List<T> getObjectListFromEntities(Class<T> expectedReturnType, List<Entity> entities) throws CentaurException {
        if (entities == null) {
            return null;
        }

        List<T> objectList = new ArrayList<>();
        for (Entity entity : entities) {
            T object = entityTranslator.fromEntity(entity, expectedReturnType);
            objectList.add(object);
        }

        return objectList;
    }

    @Override
    public <T> List<T> findObjects(String kind, Map<String, Object> keyValues, Class<T> expectedReturnType) throws CentaurException {
        return findObjectsByPropertiesSorted(kind, keyValues, expectedReturnType, (SortCriteria) null);
    }

    @Override
    public <T> List<T> getObjects(Class<T> expectedReturnType) throws CentaurException {
        return getObjects(expectedReturnType.getSimpleName(), expectedReturnType);
    }

    @Override
    public QueryResultList<Entity> findEntitiesByFilterSorted(String kind, com.google.appengine.api.datastore.Query.Filter filter, List<SortCriteria> sortCriteria, FetchOptions fetchOptions) throws CentaurException {
        return dao.findEntitiesByFilterSorted(kind, filter, sortCriteria, fetchOptions);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ResultList<T> findObjectsByFilterSorted(Class expectedReturnType, com.google.appengine.api.datastore.Query.Filter filter, List<SortCriteria> sortCriteria, FetchOptions fetchOptions) throws CentaurException {
        ResultList<T> results = new ResultList<>();

        QueryResultList<Entity> entityList = findEntitiesByFilterSorted(expectedReturnType.getSimpleName(), filter, sortCriteria, fetchOptions);

        if (entityList == null) {
            return results;
        }

        results.setIndexList(entityList.getIndexList());
        results.setCursor(entityList.getCursor());

        results.addAll(getObjectListFromEntities(expectedReturnType, entityList));

        return results;
    }

    @Override
    public <T> ResultList<T> findObjectsByFilter(Class expectedReturnType, com.google.appengine.api.datastore.Query.Filter filter) throws CentaurException {
        return findObjectsByFilterSorted(expectedReturnType, filter, null, null);
    }

    @Override
    public <T> SearchResults<T> search(Class<T> expectedReturnType, com.google.appengine.api.search.Query query) throws CentaurException {
        Results<ScoredDocument> searchResults = index.search(expectedReturnType, query);
        
        List<T> results = fromDocumentList(searchResults.getResults(), expectedReturnType);
        
        return new SearchResults<>(
                searchResults.getOperationResult(),
                results,
                searchResults.getNumberFound(),
                searchResults.getNumberReturned(),
                searchResults.getCursor());
    }
    
    protected <T> List<T> fromDocumentList(Collection<ScoredDocument> documents, Class<T> expectedReturnType) throws CentaurException {
        List<T> results = new ArrayList<>();
        for (ScoredDocument document : documents) {
            results.add(documentTranslator.fromDocument(document, expectedReturnType));
        }
        
        return results;
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

    protected void setDocumentTranslator(DocumentTranslator documentTranslator1) {
        this.documentTranslator = documentTranslator1;
    }

    protected void setCache(CentaurCache cache) {
        this.cache = cache;
    }

    protected void setIndex(CentaurIndex index) {
        this.index = index;
    }
}
