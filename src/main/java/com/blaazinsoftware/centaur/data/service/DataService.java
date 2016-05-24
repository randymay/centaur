package com.blaazinsoftware.centaur.data.service;

import com.blaazinsoftware.centaur.data.QueryResults;
import com.blaazinsoftware.centaur.data.QuerySearchOptions;
import com.blaazinsoftware.centaur.data.entity.AbstractEntity;
import com.google.appengine.api.datastore.Query;
import com.googlecode.objectify.Key;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Randy May
 */
public interface DataService {

    /**
     * Saves the provided entity into Google App Engine's DataStore.
     *
     * @param entity - Entity to Save
     * @param <T>    - Type Parameter
     * @return Identifier of the Entity from Google App Engine.
     * This Identifier (along with the entity's class) can be used to retrieve this entity
     * in the future.
     */
    <T> long saveForId(T entity);

    /**
     * Saves the provided entity into Google App Engine's DataStore.
     *
     * @param entity - Entity to Save
     * @param <T>    - Type Parameter
     * @return Web-safe String representation of the Key from Google App Engine.
     * This is the key that can be passed as the <code>keyString</code> parameter
     * in the future
     */
    <T> String saveForKey(T entity);

    /**
     * Saves the Collection of entities into Google App Engine's DataStore
     *
     * @param entities - Entitied to Save
     * @param <T>      - Type Parameter
     * @return Map containing the Key and Value of the saved entities
     */
    <T> Map<Key<T>, T> saveAll(Collection<T> entities);

    /**
     * Retrieves an entity using the String representation of its Google App Engine Key.
     *
     * @param id                 - entity identifier
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The requested entity
     */
    <T> T getEntity(Long id, Class<T> expectedReturnType);

    /**
     * Retrieves an entity using the String representation of its Google App Engine Key.
     *
     * @param keyString - Google App Engine Web Safe Key String
     * @param <T>       - Type Parameter
     * @return - The requested entity
     */
    <T> T getEntity(String keyString);

    /**
     * Retrieves an entity using the String representation of its Google App Engine Key.
     *
     * @param ids                - entity identifiers
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The requested entity
     */
    <T> Map<Long, T> getEntitiesByIds(List<Long> ids, Class<T> expectedReturnType);

    /**
     * Retrieves an entity using the String representation of its Google App Engine Key.
     *
     * @param keyStrings         - Google App Engine Key String
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The requested entity
     */
    <T> Map<String, T> getEntitiesByKeys(List<String> keyStrings, Class<T> expectedReturnType);

    /**
     * Find an entity by <code>String</code> property name and <code>entity</code> value.
     *
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The requested entity
     */
    <T> T findEntityByProperty(String propertyName, Object value, Class<T> expectedReturnType);

    /**
     * Delete the provided entity
     *
     * @param entity - Entity to delete.
     * @param <T>    - Type Parameter
     */
    <T> void deleteEntity(T entity);

    /**
     * Convenience method to delete a desired entity by using the Identifier and Class
     * of the entity
     *
     * @param id          - Id of the entity that is to be deleted
     * @param <T>         - Entity type
     * @param entityClass - Class of the entity to be deleted
     */
    <T> void deleteEntity(long id, Class<T> entityClass);

    /**
     * Convenience method to delete a desired entity by using the String representation
     * of the entity's Google Data Store Key
     *
     * @param keyString - Key of the entity that is to be deleted
     */
    void deleteEntity(String keyString);

    /**
     * Returns all children of the provided kind for the given parent.
     *
     * @param parent             - Parent entity
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @param <X>                - Type Parameter
     * @return - The <code>List</code> of children
     */
    <T, X> List<T> getAllChildren(X parent, Class<T> expectedReturnType);

    /**
     * Finds an entity by the provided <code>QuerySearchOptions</code>
     *
     * @param searchOptions - Search Options
     * @param <T>           - Type Parameter
     * @return - The <code>ListResults</code> of found entitys
     */
    <T> QueryResults<T> findEntities(QuerySearchOptions<T> searchOptions);

    /**
     * Finds an entity by the provided <code>property</code>, <code>value</code>
     *
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The <code>ListResults</code> of found entitys
     */
    <T> T findEntity(String propertyName, Object value, Class<T> expectedReturnType);

    /**
     * Returns a <code>ResultList</code> of entitys for the provided
     * <code>propertyName</code>, <code>value</code>
     *
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The <code>ListResults</code> of found entitys
     */
    <T> QueryResults<T> findEntities(String propertyName, Object value, Class<T> expectedReturnType);

    /**
     * Returns a sorted <code>ResultList</code> of entitys for the provided
     * <code>propertyName</code>, <code>value</code>
     *
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @param sortField          - Name of the property to be sorted on
     * @param <T>                - Entity type
     * @return - The <code>ListResults</code> of found entities
     */
    <T> QueryResults<T> findEntities(String propertyName, Object value, Class<T> expectedReturnType, String sortField);

    /**
     * Returns a <code>ResultList</code> of entitys for the provided
     * <code>propertyName</code>, <code>value</code>
     *
     * @param filter             - filter to apply to the query
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The <code>ListResults</code> of found entitys
     */
    <T> QueryResults<T> findEntities(Query.Filter filter, Class<T> expectedReturnType);

    /**
     * Returns a <code>ResultList</code> of entitys for the provided <code>expectedReturnType</code>
     *
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The <code>ListResults</code> of found entitys
     */
    <T> QueryResults<T> findEntities(Class<T> expectedReturnType);

    /**
     * Returns a list of Entities that match the given String representations of their
     * Google App Engine Key
     *
     * @param keyStrings         - String representation of a Google App Engine Key
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Entity type
     *
     * @return - The <code>Map</code> of found entities where the key is the Web-safe
     * key for the entity, and the value is the entity itself.
     */
    <T> Map<String, T> getEntities(List<String> keyStrings, Class<T> expectedReturnType);

    /**
     * Returns the given <code>AbstractEntity</code>'s Web-Safe Key String
     *
     * @param entity            - The Entity to get the web-safe key string from
     * @param <T>               - Entity Type
     *
     * @return - The Web-Safe Key String of the provided Entity
     */
    <T extends AbstractEntity> String getWebSafeString(T entity);
}