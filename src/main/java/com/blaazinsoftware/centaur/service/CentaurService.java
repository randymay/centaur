package com.blaazinsoftware.centaur.service;

import com.blaazinsoftware.centaur.exception.CentaurException;
import com.blaazinsoftware.centaur.search.ListResults;
import com.blaazinsoftware.centaur.search.QuerySearchOptions;
import com.google.appengine.api.datastore.Query;

import java.util.List;
import java.util.Map;

/**
 * @author Randy May
 */
public interface CentaurService {

    /**
     * Retrieves an object from the cache using the provided <code>String</code> key.
     *
     * @param keyString - Key to retrieve
     * @param <T>       - Object type
     * @return - Object from cache
     * @throws CentaurException - Exception
     */
    <T> T getEntityFromCacheByKey(String keyString) throws CentaurException;

    /**
     * Places an object into the cache.
     *
     * @param objectToCache - Object to Cache
     * @param <T>           - Object Type
     * @throws CentaurException - Exception
     */
    <T> void cacheEntity(T objectToCache) throws CentaurException;

    /**
     * Saves the provided object
     *
     * @param object - Object to Save
     * @param <T>    - Type Parameter
     * @return String representation of the Key from Google App Engine.
     * This is the key that can be passed as the <code>keyString</code> parameter
     * in the future
     * @throws CentaurException - Exception
     */
    <T> long saveForId(T object) throws CentaurException;

    /**
     * Saves the provided object, using the provided transaction.
     *
     * @param object - Object to Save
     * @param <T>    - Type Parameter
     * @return String representation of the Key from Google App Engine.
     * This is the key that can be passed as the <code>keyString</code> parameter
     * in the future
     * @throws CentaurException - Exception
     */
    <T> String saveForKey(T object) throws CentaurException;

    /**
     * Saves the provided object as a child object of the provided parent object.
     * Note: The <code>parent</code> object must be a managed object of the Data Store.
     *
     * @param parent - Parent Object
     * @param object - Object to Save
     *               @param <T> - Type Parameter
     * @return String representation of the Key from Google App Engine for the child object.
     * This is the key that can be passed as the <code>keyString</code> parameter
     * in the future
     * @throws CentaurException     - Exception
     */
    /*<T, X> String saveChild(X parent, T object) throws CentaurException;*/

    /**
     * Saves the provided object as a child object of the provided parent object, using the provided transaction.
     * Note: The <code>parent</code> object must be a managed object of the Data Store.
     *
     * @param parent      - Parent Object
     * @param object      - Object to Save
     * @param transaction - Transaction to use
     * @return String representation of the Key from Google App Engine for the child object.
     * This is the key that can be passed as the <code>keyString</code> parameter
     * in the future
     * @throws CentaurException      - Exception
     *//*
    <T, X> String saveChild(X parent, T object, Transaction transaction) throws CentaurException;*/

    /**
     * Retrieves an object using the String representation of its Google App Engine Key.
     *
     * @param id                 - object identifier
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The requested object
     * @throws CentaurException - Exception
     */
    <T> T getEntity(Long id, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Retrieves an object using the String representation of its Google App Engine Key.
     *
     * @param keyString - Google App Engine Web Safe Key String
     * @param <T>       - Type Parameter
     * @return - The requested object
     * @throws CentaurException - Exception
     */
    <T> T getEntity(String keyString) throws CentaurException;

    /**
     * Retrieves an object using the String representation of its Google App Engine Key.
     *
     * @param ids                - object identifiers
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The requested object
     * @throws CentaurException - Exception
     */
    <T> Map<Long, T> getEntitiesByIds(List<Long> ids, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Retrieves an object using the String representation of its Google App Engine Key.
     *
     * @param keyStrings         - Google App Engine Key String
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The requested object
     * @throws CentaurException - Exception
     */
    <T> Map<String, T> getEntitiesByKeys(List<String> keyStrings, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Find an object by <code>property name</code> and <code>value</code>
     *
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The requested object
     * @throws CentaurException - Exception
     */
    <T> T findEntityByProperty(String propertyName, Object value, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Convenience method for finding an object by <code>userId</code>.
     * This is the same as calling:
     * <code>findEntityByProperty("userId", value, expectedReturnType);</code>
     *
     * @param userId             - Name of the property to search on
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The requested object
     * @throws CentaurException - Exception
     */
    <T> T findEntityByUserId(String userId, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Delete the provided object
     *
     * @param object - Object to delete.
     * @param <T>    - Type Parameter
     * @throws CentaurException - Exception
     */
    <T> void deleteEntity(T object) throws CentaurException;

    /**
     * Convenience method to delete a desired object by using the String representation
     * of the object's Google Data Store Key
     *
     * @param id - Id of the object that is to be deleted
     * @throws CentaurException - Exception
     */
    void deleteEntity(long id) throws CentaurException;

    /**
     * Convenience method to delete a desired object by using the String representation
     * of the object's Google Data Store Key
     *
     * @param keyString - Key of the object that is to be deleted
     * @throws CentaurException - Exception
     */
    void deleteEntity(String keyString) throws CentaurException;

    /**
     * Returns all children of the provided kind for the given parent.
     *
     * @param parent             - Parent object
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @param <X>                - Type Parameter
     * @return - The <code>List</code> of children
     * @throws CentaurException - Exception
     */
    <T, X> List<T> getAllChildren(X parent, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Returns all children of the provided kind for the given parent.
     *
     * @param parent             - Parent object
     * @param expectedReturnType - Return type of the result
     * @param dataOptions        - <code>DataOptions</code> to apply
     * @return - The <code>ResultList</code> of children
     * @throws CentaurException           - Exception
     *//*
    <T, X> List<T> getAllChildren(X parent, Class<T> expectedReturnType, DataOptions dataOptions) throws CentaurException;

    *//**
     * Returns all children of the provided kind for the given parent.
     *
     * @param parent             - Parent object
     * @param expectedReturnType - Return type of the result
     * @param fetchOptions       - <code>FetchOptions</code> to apply
     * @param dataOptions        - <code>DataOptions</code> to apply
     * @return - The <code>ResultList</code> of children
     * @throws CentaurException          - Exception
     *//*
    <T, X> List<T> getAllChildren(X parent, Class<T> expectedReturnType, FetchOptions fetchOptions, DataOptions dataOptions) throws CentaurException;*/

    /**
     * Returns all children of the provided kind for the given parent.
     *
     * @param parent             - Parent object
     * @param expectedReturnType - Return type of the result
     * @return - The <code>ListResults</code> of found objects
     * @throws CentaurException           - Exception
     */
    /*<T, X> List<T> getAllChildren(X parent, Class<T> expectedReturnType) throws CentaurException;*/

    /**
     * Returns all children of the provided kind for the given parent.
     *
     * @param kind               - Kind of object to be found
     * @param parent             - Parent object
     * @param expectedReturnType - Return type of the result
     * @param fetchOptions       - <code>FetchOptions</code> to apply
     * @param dataOptions        - <code>DataOptions</code> to apply
     * @return - The <code>ListResults</code> of found objects
     * @throws CentaurException           - Exception
     *//*
    <T, X> List<T> getAllChildren(X parent, Class<T> expectedReturnType, FetchOptions fetchOptions, DataOptions dataOptions) throws CentaurException;*/

    /**
     * Finds an object by the provided <code>QuerySearchOptions</code>
     *
     * @param searchOptions - Search Options
     * @param <T>           - Type Parameter
     * @return - The <code>ListResults</code> of found objects
     * @throws CentaurException - Exception
     */
    <T> ListResults<T> findEntities(QuerySearchOptions<T> searchOptions) throws CentaurException;

    /**
     * Finds an object by the provided <code>property</code>, <code>value</code>
     *
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The <code>ListResults</code> of found objects
     * @throws CentaurException - Exception
     */
    <T> T findEntity(String propertyName, Object value, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Returns a <code>ResultList</code> of objects for the provided
     * <code>propertyName</code>, <code>value</code>
     *
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The <code>ListResults</code> of found objects
     * @throws CentaurException - Exception
     */
    <T> ListResults<T> findEntities(String propertyName, Object value, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Returns a sorted <code>ResultList</code> of objects for the provided
     * <code>propertyName</code>, <code>value</code>
     *
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @param sortField          - Name of the property to be sorted on
     * @return - The <code>ListResults</code> of found objects
     * @throws CentaurException         - Exception
     */
    //<T> ListResults<T> findEntities(String propertyName, Object value, Class<T> expectedReturnType, String sortField) throws CentaurException;

    /**
     * Returns a sorted <code>ResultList</code> of objects for the provided
     * <code>propertyName</code>, <code>value</code>
     *
     * @param kind               - Kind of objects to be found
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @param sortCriteria       - Sort Criteria to be applied
     * @return - The <code>ListResults</code> of found objects
     * @throws CentaurException         - Exception
     *//*
    <T> List<T> findObjectsSorted(String propertyName, Object value, Class<T> expectedReturnType, SortCriteria... sortCriteria) throws CentaurException;

    *//**
     * Returns a sorted <code>ResultList</code> of objects for the provided
     * <code>propertyName</code>, <code>value</code>
     *
     * @param kind               - Kind of objects to be found
     * @param keyValues          - <code>Map</code> where the key is the name of the property to search on, and the
     *                           value is the value that the property is to match
     * @param expectedReturnType - Return type of the result
     * @param sortCriteria       - Sort Criteria to be applied
     * @return - The <code>ListResults</code> of found objects
     * @throws CentaurException        - Exception
     *//*
    <T> List<T> findObjectsByPropertiesSorted(Map<String, Object> keyValues, Class<T> expectedReturnType, SortCriteria... sortCriteria) throws CentaurException;*/

    /**
     * Returns a <code>ResultList</code> of objects for the provided
     * <code>propertyName</code>, <code>value</code>
     *
     * @param filter             - filter to apply to the query
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The <code>ListResults</code> of found objects
     * @throws CentaurException - Exception
     */
    <T> ListResults<T> findEntities(Query.Filter filter, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Returns a <code>ResultList</code> of objects for the provided <code>expectedReturnType</code>
     *
     * @param expectedReturnType - Return type of the result
     * @param <T>                - Type Parameter
     * @return - The <code>ListResults</code> of found objects
     * @throws CentaurException - Exception
     */
    <T> ListResults<T> findEntities(Class<T> expectedReturnType) throws CentaurException;

    /**
     * Returns a list of Objects that match the given String representations of their Google App Engine Key
     *
     * @param keyStrings         - String representation of a Google App Engine Key
     * @param expectedReturnType - Return type of the result
     * @return - The <code>ListResults</code> of found objects
     * @throws CentaurException         - Exception
     *//*
    <T> Map<String, T> getObjectByKeyStrings(List<String> keyStrings, Class<T> expectedReturnType) throws CentaurException;

    *//**
     * Returns a list of Objects that match the given Google App Engine Keys
     *
     * @param keys               - String representation of a Google App Engine Key
     * @param expectedReturnType - Return type of the result
     * @return - The <code>ListResults</code> of found objects
     * @throws CentaurException
     *//*
    <T> Map<String, T> getObjectByKeys(List<Key> keys, Class<T> expectedReturnType) throws CentaurException;

    *//**
     * Returns a sorted <code>ResultList</code> of <code>Entity</code>s for the provided
     * <code>filter</code>
     *
     * @param kind         - Kind of objects to be found
     * @param filter       - <code>filter</code> to be applied
     * @param sortCriteria - Sort Criteria to be applied
     * @param fetchOptions - <code>FetchOptions</code> to be applied
     * @return - The <code>ListResults</code> of found objects
     * @throws CentaurException
     *//*
    QueryResultList<Entity> findEntitiesByFilterSorted(Query.Filter filter, List<SortCriteria> sortCriteria, FetchOptions fetchOptions) throws CentaurException;

    *//**
     * Returns a sorted <code>ResultList</code> of <code>Entity</code>s for the provided
     * <code>expectedReturnType</code>, <code>filter</code>
     *
     * @param expectedReturnType - Return type of the result
     * @param filter             - <code>filter</code> to be applied
     * @param sortCriteria       - Sort Criteria to be applied
     * @param fetchOptions       - <code>FetchOptions</code> to be applied
     * @return - The <code>ListResults</code> of found objects
     * @throws CentaurException
     *//*
    <T> ResultList<T> findObjectsByFilterSorted(Class expectedReturnType, Query.Filter filter, List<SortCriteria> sortCriteria, FetchOptions fetchOptions) throws CentaurException;

    *//**
     * Returns a <code>ResultList</code> of <code>Entity</code>s for the provided
     * <code>expectedReturnType</code>, <code>filter</code>
     *
     * @param expectedReturnType - Return type of the result
     * @param filter             - <code>filter</code> to be applied
     * @return - The <code>ListResults</code> of found objects
     * @throws CentaurException
     *//*
    <T> ResultList<T> findObjectsByFilter(Class expectedReturnType, Query.Filter filter) throws CentaurException;

    <T> void indexDocument(Document document, Class<T> expectedReturnType) throws CentaurException;

    void indexDocument(Document document, String indexName) throws CentaurException;

    <T> SearchResults<T> search(Class<T> expectedReturnType, com.google.appengine.api.search.Query query) throws CentaurException;

    *//**
     * Creates and returns a <code>Transaction</code>
     *
     * @return a <code>Transaction</code>
     *//*
    Transaction beginTransaction();

    *//**
     * Creates and returns a Cross-Group<code>transaction</code>
     *
     * @return a <code>Transaction</code>
     *//*
    Transaction beginCrossGroupTransaction();

    *//**
     * Rolls back the provided <code>transaction</code>
     *
     * @param transaction - The <code>Transaction</code> to rollback
     *//*
    void rollback(Transaction transaction);

    *//**
     * Commits the provided <code>transaction</code>
     *
     * @param transaction - The <code>Transaction</code> to commit
     *//*
    void commit(Transaction transaction);*/
}
