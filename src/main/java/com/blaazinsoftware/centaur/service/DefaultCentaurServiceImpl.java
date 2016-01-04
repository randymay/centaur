package com.blaazinsoftware.centaur.service;

import com.blaazinsoftware.centaur.data.dao.BasicDAO;
import com.blaazinsoftware.centaur.exception.CentaurException;
import com.blaazinsoftware.centaur.search.ListResults;
import com.blaazinsoftware.centaur.search.QuerySearchOptions;
import com.google.appengine.api.datastore.Query;
import com.googlecode.objectify.Work;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Default Implementation of <code>CentaurService</code>
 *
 * @author Randy May
 */
public class DefaultCentaurServiceImpl implements CentaurService {

    private BasicDAO dao = new BasicDAO();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getEntityFromCacheByKey(String keyString) throws CentaurException {
        return (T) dao.loadFromCache(keyString);
    }

    @Override
    public <T> void cacheEntity(T objectToCache) throws CentaurException {
        dao.cacheEntity(objectToCache);
    }

    @Override
    public <T> long saveForId(T object) throws CentaurException {
        return dao.saveForId(object);
    }

    @Override
    public <T> String saveForKey(T object) throws CentaurException {
        return dao.saveForKey(object);
    }

    //@Override
    public <T> T executeInTransaction(Work<T> work) throws CentaurException {
        return dao.executeWorkInTransaction(work);
    }

    @Override
    public <T> T getEntity(Long id, Class<T> expectedReturnType) throws CentaurException {
        return dao.load(id, expectedReturnType);
    }

    @Override
    public <T> T getEntity(String keyString) throws CentaurException {
        return dao.load(keyString);
    }

    @Override
    public <T> T findEntityByProperty(String propertyName, Object value, Class<T> expectedReturnType) throws CentaurException {
        return this.findEntity(propertyName, value, expectedReturnType);
    }

    @Override
    public <T> T findEntityByUserId(String userId, Class<T> expectedReturnType) throws CentaurException {
        return findEntity("userId", userId, expectedReturnType);
    }

    @Override
    public <T> T findEntity(String propertyName, Object value, Class<T> expectedReturnType) throws CentaurException {
        QuerySearchOptions<T> searchOptions = new QuerySearchOptions<>(expectedReturnType);
        Query.Filter filter = new Query.FilterPredicate(propertyName, Query.FilterOperator.EQUAL, value);
        searchOptions
                .returnType(expectedReturnType)
                .filter(filter);

        ListResults<T> results = dao.getPagedList(searchOptions);
        if (results.getCountFound() < 1) {
            return null;
        }

        return results.getResults().get(0);
    }

    @Override
    public <T> ListResults<T> findEntities(Class<T> expectedReturnType) throws CentaurException {
        QuerySearchOptions<T> searchOptions = new QuerySearchOptions<>(expectedReturnType);
        return dao.getPagedList(searchOptions);
    }

    @Override
    public <T> Map<Long, T> getEntitiesByIds(List<Long> ids, Class<T> expectedReturnType) throws CentaurException {
        return dao.loadByIds(ids, expectedReturnType);
    }

    @Override
    public <T> Map<String, T> getEntitiesByKeys(List<String> keyStrings, Class<T> expectedReturnType) throws CentaurException {
        return dao.loadByKeys(keyStrings, expectedReturnType);
    }

    @Override
    public <T> void deleteEntity(T object) throws CentaurException {
        dao.delete(object);
    }

    @Override
    public void deleteEntity(long id) throws CentaurException {
        dao.delete(id);
    }

    @Override
    public void deleteEntity(String keyString) throws CentaurException {
        dao.delete(keyString);
    }

    @Override
    public <T, X> List<T> getAllChildren(X parent, Class<T> expectedReturnType) throws CentaurException {
        return dao.loadChildren(expectedReturnType, parent);
    }

    public <T, P> List<T> findChildrenByFilter(Class<T> entityClass, P parentClass, String fieldName, Object filterObject) {
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put(fieldName, filterObject);
        return findChildrenByFilter(entityClass, parentClass, filterMap);
    }

    public <T, P> List<T> findChildrenByFilter(Class<T> entityClass, P parentClass, Map<String, Object> filterMap) {
        com.googlecode.objectify.cmd.Query<T> query = ofy().load().type(entityClass).ancestor(parentClass);
        for (Map.Entry<String, Object> entry : filterMap.entrySet()) {
            query = query.filter(entry.getKey(), entry.getValue());
        }
        return query.list();
    }

    /*@Override
    public <T, X> List<T> getAllChildren(X parent, Class<T> expectedReturnType, DataOptions dataOptions) throws CentaurException {
        return getAllChildren(parent, expectedReturnType, null, dataOptions);
    }

    @Override
    public <T, X> List<T> getAllChildren(X parent, Class<T> expectedReturnType, FetchOptions fetchOptions, DataOptions dataOptions) throws CentaurException {
        return getAllChildren(null, parent, expectedReturnType, fetchOptions, dataOptions);
    }

    @Override
    public <T, X> List<T> getAllChildren(String kind, X parent, Class<T> expectedReturnType) throws CentaurException {
        return getAllChildren(kind, parent, expectedReturnType, null, null);
    }

    @Override
    public <T, X> List<T> getAllChildren(String kind, X parent, Class<T> expectedReturnType, FetchOptions fetchOptions, DataOptions dataOptions) throws CentaurException {
        //dao.f
        *//*try {
            final Key key = CentaurServiceUtils.getKey(parent);
            if (null == parent || null == key) {
                throw new CentaurException("Parent is null, or no Key field found");
            }

            if (null == kind) {
                final T object = expectedReturnType.newInstance();
                kind = CentaurServiceUtils.getKindValue(object);

            }

            Entity parentEntity = entityTranslator.toEntity(parent);
            List<Entity> entities = dao.getAllChildren(kind, parentEntity, fetchOptions, dataOptions);

            List<T> results = new ArrayList<>();
            if (null == entities) {
                return null;
            }

            for (Entity entity : entities) {
                T object = entityTranslator.fromEntity(entity, expectedReturnType);
                results.add(object);
            }

            return results;
        } catch (Exception e) {
            throw new CentaurException(e);
        }*//*
    }

    @Override
    public <T> List<T> findEntities(String kind, Class<T> expectedReturnType) throws CentaurException {
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
    }*/

    @Override
    public <T> ListResults<T> findEntities(String propertyName, Object value, Class<T> expectedReturnType) throws CentaurException {
        Query.Filter filter = new Query.FilterPredicate(propertyName, Query.FilterOperator.EQUAL, value);

        return findEntities(filter, expectedReturnType);
    }

    @Override
    public <T> ListResults<T> findEntities(Query.Filter filter, Class<T> expectedReturnType) throws CentaurException {
        QuerySearchOptions<T> searchOptions = new QuerySearchOptions<>(expectedReturnType);
        searchOptions
                .returnType(expectedReturnType)
                .filter(filter);

        return this.findEntities(searchOptions);
    }

    @Override
    public <T> ListResults<T> findEntities(QuerySearchOptions<T> searchOptions) throws CentaurException {
        return dao.getPagedList(searchOptions);
    }

    /*@Override
    public <T> List<T> findEntities(String kind, String propertyName, Object value, Class<T> expectedReturnType, String sortField) throws CentaurException {
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
    public <T> List<T> findEntities(String kind, Map<String, Object> keyValues, Class<T> expectedReturnType) throws CentaurException {
        return findObjectsByPropertiesSorted(kind, keyValues, expectedReturnType, (SortCriteria) null);
    }*/

    /*@Override
    public <T> ListResults<T> findEntities(Class<T> expectedReturnType) throws CentaurException {
        QuerySearchOptions<T> searchOptions = new QuerySearchOptions<>();
        searchOptions.returnType(expectedReturnType);

        return dao.findEntities(searchOptions);
    }*/

    /*@Override
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
        return ofy().getTransaction();
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
    }*/
}
