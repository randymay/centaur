package com.blaazinsoftware.centaur.data.service;

import com.blaazinsoftware.centaur.dao.BasicDAO;
import com.blaazinsoftware.centaur.data.QueryResults;
import com.blaazinsoftware.centaur.data.QuerySearchOptions;
import com.blaazinsoftware.centaur.data.entity.AbstractEntity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Default Implementation of <code>CentaurService</code>
 *
 * @author Randy May
 */
public class DefaultDataServiceImpl implements DataService {

    private BasicDAO dao = new BasicDAO();

    protected DefaultDataServiceImpl() {
    }

    @Override
    public <T> long saveForId(T entity) {
        return dao.saveForId(entity);
    }

    @Override
    public <T> String saveForWebSafeKey(T object) {
        return dao.saveForKey(object);
    }

    @Override
    public <T> Map<Key<T>, T> saveAll(Collection<T> entities) {
        return dao.saveAll(entities);
    }

    //@Override
    public <T> T executeInTransaction(Work<T> work) {
        return dao.executeWorkInTransaction(work);
    }

    @Override
    public <T> T getEntity(Long id, Class<T> expectedReturnType) {
        return dao.loadEntity(id, expectedReturnType);
    }

    @Override
    public <T> Key<T> getKey(long id, Class<T> expectedReturnType) {
        return dao.getKey(expectedReturnType, id);
    }

    @Override
    public <T> Key<T> getKey(String name, Class<T> expectedReturnType) {
        return dao.getKey(expectedReturnType, name);
    }

    @Override
    public <T> T getEntity(String id, Class<T> expectedReturnType) {
        return dao.loadEntity(id, expectedReturnType);
    }

    @Override
    public <T> T getEntityByWebSafeKey(String webSafeKeyString) {
        return (T)dao.loadEntity(Key.create(webSafeKeyString));
    }

    @Override
    public <T> T findEntityByProperty(String propertyName, Object value, Class<T> expectedReturnType) {
        return this.findEntity(propertyName, value, expectedReturnType);
    }

    @Override
    public <T> T findEntity(String propertyName, Object value, Class<T> expectedReturnType) {
        QuerySearchOptions<T> searchOptions = new QuerySearchOptions<>(expectedReturnType);
        Query.Filter filter = new Query.FilterPredicate(propertyName, Query.FilterOperator.EQUAL, value);
        searchOptions
                .returnType(expectedReturnType)
                .filter(filter);

        QueryResults<T> results = dao.getPagedList(searchOptions);
        if (results.getCountFound() < 1) {
            return null;
        }

        return results.getResults().get(0);
    }

    @Override
    public <T> QueryResults<T> findEntities(Class<T> expectedReturnType) {
        QuerySearchOptions<T> searchOptions = new QuerySearchOptions<>(expectedReturnType);
        return dao.getPagedList(searchOptions);
    }

    @Override
    public <T> Map<Long, T> getEntitiesByIds(List<Long> ids, Class<T> expectedReturnType) {
        return dao.loadByIds(ids, expectedReturnType);
    }

    @Override
    public <T> Map<String, T> getEntitiesByKeys(List<String> keyStrings, Class<T> expectedReturnType) {
        return dao.loadByKeys(keyStrings, expectedReturnType);
    }

    @Override
    public <T> void deleteEntity(T object) {
        dao.delete(object);
    }

    @Override
    public <T> void deleteEntity(long id, Class<T> entityClass) {
        dao.delete(id, entityClass);
    }

    @Override
    public void deleteEntity(String keyString) {
        dao.delete(keyString);
    }

    @Override
    public <T, X> List<T> getAllChildren(X parent, Class<T> childClass) {
        return dao.loadChildren(childClass, parent);
    }

    @Override
    public <T, X> T getChild(long id, X parent, Class<T> childClass) {
        return dao.loadChild(id, childClass, parent);
    }

    @Override
    public <T, X> T getChild(String id, X parent, Class<T> childClass) {
        return dao.loadChild(id, childClass, parent);
    }

    public <T, P> List<T> findChildrenByFilter(Class<T> childClass, P parentClass, String fieldName, Object filterObject) {
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put(fieldName, filterObject);
        return findChildrenByFilter(childClass, parentClass, filterMap);
    }

    public <T, P> List<T> findChildrenByFilter(Class<T> childClass, P parentClass, Map<String, Object> filterMap) {
        com.googlecode.objectify.cmd.Query<T> query = ofy().load().type(childClass).ancestor(parentClass);
        for (Map.Entry<String, Object> entry : filterMap.entrySet()) {
            query = query.filter(entry.getKey(), entry.getValue());
        }
        return query.list();
    }

    @Override
    public <T> QueryResults<T> findEntities(String propertyName, Object value, Class<T> expectedReturnType) {
        Query.Filter filter = new Query.FilterPredicate(propertyName, Query.FilterOperator.EQUAL, value);

        return findEntities(filter, expectedReturnType);
    }

    @Override
    public <T> QueryResults<T> findEntities(Query.Filter filter, Class<T> expectedReturnType) {
        QuerySearchOptions<T> searchOptions = new QuerySearchOptions<>(expectedReturnType);
        searchOptions
                .returnType(expectedReturnType)
                .filter(filter);

        return this.findEntities(searchOptions);
    }

    @Override
    public <T> QueryResults<T> findEntities(QuerySearchOptions<T> searchOptions) {
        return dao.getPagedList(searchOptions);
    }

    @Override
    public <T> QueryResults<T> findEntities(String propertyName, Object value, Class<T> expectedReturnType, String sortField) {
        QuerySearchOptions<T> searchOptions = new QuerySearchOptions<>(expectedReturnType);

        Query.Filter filter = new Query.FilterPredicate(propertyName, Query.FilterOperator.EQUAL, value);
        searchOptions.filter(filter);

        return findEntities(searchOptions);
    }

    @Override
    public <T> Map<String, T> getEntities(List<String> keyStrings, Class<T> expectedReturnType) {
        return dao.loadByKeys(keyStrings, expectedReturnType);
    }

    @Override
    public <T extends AbstractEntity> String getWebSafeString(T entity) {
        return KeyFactory.keyToString(KeyFactory.createKey(entity.getClass().getSimpleName(), entity.getId()));
    }
}
