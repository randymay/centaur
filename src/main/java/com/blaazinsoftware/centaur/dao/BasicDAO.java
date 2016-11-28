package com.blaazinsoftware.centaur.dao;

import com.blaazinsoftware.centaur.data.QueryResults;
import com.blaazinsoftware.centaur.data.QuerySearchOptions;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Query;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * @author Randy May
 *         Date: 15-09-28
 */
public class BasicDAO {
    public <T> long saveForId(T entity) {
        return ofy().save().entity(entity).now().getId();
    }

    public <T> String saveForKey(T entity) {
        return ofy().save().entity(entity).now().toWebSafeString();
    }

    public <T> Map<Key<T>, T> saveAll(Collection<T> entities) {
        return ofy().save().entities(entities).now();
    }

    public <T> void delete(T entity) {
        ofy().delete().entity(entity).now();
    }

    public <T> void delete(Long id, Class<T> entityClass) {
        ofy().delete().type(entityClass).id(id).now();
    }

    public <T> void delete(String keyString) {
        Key<T> key = getKey(keyString);
        ofy().delete().key(key).now();
    }

    public <T> T loadEntity(long id, Class<T> entityClass) {
        return ofy().load().type(entityClass).id(id).now();
    }

    public <T> T loadEntity(String id, Class<T> entityClass) {
        return ofy().load().type(entityClass).id(id).now();
    }

    public <T> T loadEntity(Key<T> key) {
        return ofy().load().key(key).now();
    }

    private <T> Key<T> getKey(String keyString) {
        return Key.create(keyString);
    }

    public <T> Key<T> getKey(Class<T> entityClass, long id) {
        return Key.create(entityClass, id);
    }

    public <T> Key<T> getKey(Class<T> entityClass, String name) {
        return Key.create(entityClass, name);
    }

    public <T> void cacheEntity(String key, T entity) {
        MemcacheService syncCache = getMemcacheService();
        syncCache.put(key, entity); // Populate cache.
    }

    public <T> void unCacheEntity(String key) {
        MemcacheService syncCache = getMemcacheService();
        syncCache.put(key, null);
    }

    private MemcacheService getMemcacheService() {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
        return syncCache;
    }

    @SuppressWarnings("unchecked")
    public <T> T loadFromCache(String keyString) {
        MemcacheService syncCache = getMemcacheService();

        Object result = syncCache.get(keyString); // Populate cache.

        return (T) result;
    }

    public <T> Map<Long, T> loadByIds(List<Long> ids, Class<T> entityClass) {
        return ofy().load().type(entityClass).ids(ids);
    }

    public <T> Map<String, T> loadByKeys(List<String> keys, Class<T> entityClass) {
        return ofy().load().type(entityClass).ids(keys);
    }

    public <T> T loadByGroup(long id, Class<T> entityClass, Class<?>... groupClass) {
        return ofy().load().group(groupClass).type(entityClass).id(id).now();
    }

    public <T, P> T loadChild(long id, Class<T> entityClass, P parentClass) {
        return ofy().load().type(entityClass).parent(parentClass).id(id).now();
    }

    public <T, P> T loadChild(String id, Class<T> entityClass, P parentClass) {
        return ofy().load().type(entityClass).parent(parentClass).id(id).now();
    }

    public <T, P> List<T> loadChildren(Class<T> entityClass, P parent) {
        return ofy().load().type(entityClass).ancestor(parent).list();
    }

    public <T> T findFirstEntity(Class<T> entityClass, String propertyName, Object o) {
        return ofy().load().type(entityClass).filter(propertyName, o).first().now();
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

    public <T, P> T loadFirstChild(Class<T> entityClass, P parentClass) {
        List<T> results = loadChildren(entityClass, parentClass);

        if (null == results || results.size() == 0) {
            return null;
        }

        return results.get(0);
    }

    public <T> T executeWorkInTransaction(Work<T> work) {
        return ofy().transact(work);
    }

    public <T> QueryResults<T> getPagedList(QuerySearchOptions<T> searchOptions) {

        Query<T> query = ofy().load().type(searchOptions.getReturnType());

        // Apply Filter
        if (null != searchOptions.getFilter() && searchOptions.getFilter().size() > 0) {
            for (Map.Entry<String, Object> entry : searchOptions.getFilter().entrySet()) {
                query = query.filter(entry.getKey(), entry.getValue());
            }
        }

        // Set Order By Field
        if (StringUtils.isNotEmpty(searchOptions.getOrderBy())) {
            String order = searchOptions.getOrderBy();
            if (!searchOptions.isDescending()) {
                order = "-" + order;
            }
            query = query.order(order);
        }

        // Apply Cursor
        final Cursor cursor = searchOptions.getCursor();
        if (cursor != null) {
            query = query.startAt(cursor);
        } else {
            // Apply Offset
            if (searchOptions.getOffset() > 0) {
                query = query.offset(searchOptions.getOffset());
            }
        }
        // Apply Limit
        if (searchOptions.getLimit() > 0) {
            query = query.limit(searchOptions.getLimit());
        }

        QueryResults<T> results = new QueryResults<>();

        QueryResultIterator<T> iterator = query.iterator();

        while (iterator.hasNext()) {
            results.getResults().add(iterator.next());
        }

        results.setCountReturned(results.getResults().size());
        if (results.getCountReturned() >= searchOptions.getLimit()) {
            // Only return the cursor if more records are available
            results.setCursor(iterator.getCursor());
        }

        // Execute a second query to determine the total number of records in this query
        Query<T> countQuery = ofy().load().type(searchOptions.getReturnType());
        if (null != searchOptions.getFilter() && searchOptions.getFilter().size() > 0) {
            for (Map.Entry<String, Object> entry : searchOptions.getFilter().entrySet()) {
                countQuery = countQuery.filter(entry.getKey(), entry.getValue());
            }
        }
        results.setCountFound(countQuery.count());

        return results;
    }
}
