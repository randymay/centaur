package com.blaazinsoftware.centaur.service;

import com.blaazinsoftware.centaur.search.QueryResults;
import com.blaazinsoftware.centaur.search.QuerySearchOptions;
import com.google.appengine.api.datastore.Query;

import java.util.List;
import java.util.Map;

/**
 * @author Randy May
 */
public interface CentaurService {

    /**
     * Cache an entity
     *
     * @param key    - Cache key
     * @param entity - Entity to cache
     * @param <T>    - Entity type
     */
    <T> void cacheEntity(String key, T entity);

    /**
     * Remove item from cache at the key provided
     *
     * @param key - Key of entity to be removed from cache
     */
    void unCacheEntity(String key);

    /**
     * Retrieves an object from the cache using the provided Web-safe <code>String</code> key.
     *
     * @param keyString - Key to retrieve
     * @param <T>       - Entity type
     * @return - Entity from cache
     */
    <T> T getEntityFromCache(String keyString);

    /**
     * Saves the provided object into Google App Engine's DataStore.
     *
     * @param entity - Entity to Save
     * @param <T>    - Type Parameter
     * @return Identifier of the Entity from Google App Engine.
     * This Identifier (along with the object's class) can be used to retrieve this object
     * in the future.
     */
    <T> long saveForId(T entity);

    /**
     * Saves the provided object into Google App Engine's DataStore.
     *
     * @param object - Entity to Save
     * @param <T>    - Type Parameter
     * @return Web-safe String representation of the Key from Google App Engine.
     * This is the key that can be passed as the <code>keyString</code> parameter
     * in the future
     */
    <T> String saveForKey(T object);

    /**
     * Retrieves an object using the String representation of its Google App Engine Key.
     *
     * @param id                 - object identifier
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The requested object
     */
    <T> T getEntity(Long id, Class<T> expectedReturnType);

    /**
     * Retrieves an object using the String representation of its Google App Engine Key.
     *
     * @param keyString - Google App Engine Web Safe Key String
     * @param <T>       - Type Parameter
     * @return - The requested object
     */
    <T> T getEntity(String keyString);

    /**
     * Retrieves an object using the String representation of its Google App Engine Key.
     *
     * @param ids                - object identifiers
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The requested object
     */
    <T> Map<Long, T> getEntitiesByIds(List<Long> ids, Class<T> expectedReturnType);

    /**
     * Retrieves an object using the String representation of its Google App Engine Key.
     *
     * @param keyStrings         - Google App Engine Key String
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The requested object
     */
    <T> Map<String, T> getEntitiesByKeys(List<String> keyStrings, Class<T> expectedReturnType);

    /**
     * Find an object by <code>String</code> property name and <code>Object</code> value.
     *
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The requested object
     */
    <T> T findEntityByProperty(String propertyName, Object value, Class<T> expectedReturnType);

    /**
     * Delete the provided object
     *
     * @param object - Entity to delete.
     * @param <T>    - Type Parameter
     */
    <T> void deleteEntity(T object);

    /**
     * Convenience method to delete a desired entity by using the Identifier and Class
     * of the entity
     *
     * @param id          - Id of the object that is to be deleted
     * @param <T>         - Entity type
     * @param entityClass - Class of the entity to be deleted
     */
    <T> void deleteEntity(long id, Class<T> entityClass);

    /**
     * Convenience method to delete a desired object by using the String representation
     * of the object's Google Data Store Key
     *
     * @param keyString - Key of the object that is to be deleted
     */
    void deleteEntity(String keyString);

    /**
     * Returns all children of the provided kind for the given parent.
     *
     * @param parent             - Parent object
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @param <X>                - Type Parameter
     * @return - The <code>List</code> of children
     */
    <T, X> List<T> getAllChildren(X parent, Class<T> expectedReturnType);

    /**
     * Finds an object by the provided <code>QuerySearchOptions</code>
     *
     * @param searchOptions - Search Options
     * @param <T>           - Type Parameter
     * @return - The <code>ListResults</code> of found objects
     */
    <T> QueryResults<T> findEntities(QuerySearchOptions<T> searchOptions);

    /**
     * Finds an object by the provided <code>property</code>, <code>value</code>
     *
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The <code>ListResults</code> of found objects
     */
    <T> T findEntity(String propertyName, Object value, Class<T> expectedReturnType);

    /**
     * Returns a <code>ResultList</code> of objects for the provided
     * <code>propertyName</code>, <code>value</code>
     *
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The <code>ListResults</code> of found objects
     */
    <T> QueryResults<T> findEntities(String propertyName, Object value, Class<T> expectedReturnType);

    /**
     * Returns a sorted <code>ResultList</code> of objects for the provided
     * <code>propertyName</code>, <code>value</code>
     *
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @param sortField          - Name of the property to be sorted on
     * @param <T>                - Entity type
     * @return - The <code>ListResults</code> of found objects
     */
    <T> QueryResults<T> findEntities(String propertyName, Object value, Class<T> expectedReturnType, String sortField);

    /**
     * Returns a <code>ResultList</code> of objects for the provided
     * <code>propertyName</code>, <code>value</code>
     *
     * @param filter             - filter to apply to the query
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The <code>ListResults</code> of found objects
     */
    <T> QueryResults<T> findEntities(Query.Filter filter, Class<T> expectedReturnType);

    /**
     * Returns a <code>ResultList</code> of objects for the provided <code>expectedReturnType</code>
     *
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The <code>ListResults</code> of found objects
     */
    <T> QueryResults<T> findEntities(Class<T> expectedReturnType);

    /**
     * Returns a list of Entities that match the given String representations of their Google App Engine Key
     *
     * @param keyStrings         - String representation of a Google App Engine Key
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Entity type
     * @return - The <code>ListResults</code> of found objects
     */
    <T> Map<String, T> getEntities(List<String> keyStrings, Class<T> expectedReturnType);
}
