package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.search.Document;
import org.blaazinsoftware.centaur.CentaurException;
import org.blaazinsoftware.centaur.query.DataOptions;
import org.blaazinsoftware.centaur.search.SortCriteria;

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
     * @throws CentaurException
     */
    public <T> T getObjectFromCacheByKey(String keyString) throws CentaurException;

    /**
     * Retrieves an object from the cache using the provided <code>Object</code> key.
     *
     * @param key - Key to retrieve
     * @param <T> - Object type
     * @return - Object from cache
     * @throws CentaurException
     */
    public <T> T getObjectFromCache(Object key) throws CentaurException;

    /**
     * Places an object into the cache using the provided <code>String</code> key.
     *
     * @param key           - Key of Object
     * @param objectToCache - Object to Cache
     * @param <T>           - Object Type
     * @throws CentaurException
     */
    public <T> void cacheObjectByKey(String key, T objectToCache) throws CentaurException;

    /**
     * Places an object into the cache using the provided <code>Object</code> key.
     *
     * @param key           - Key of Object
     * @param objectToCache - Object to Cache
     * @param <T>           - Object Type
     * @throws CentaurException
     */
    public <T> void cacheObject(Object key, T objectToCache) throws CentaurException;

    /**
     * Saves the provided object
     *
     * @param object - Object to Save
     * @return String representation of the Key from Google App Engine.
     * This is the key that can be passed as the <code>keyString</code> parameter
     * in the future
     * @throws CentaurException
     */
    public <T> String save(T object) throws CentaurException;

    /**
     * Saves the provided object, using the provided transaction.
     *
     * @param object      - Object to Save
     * @param transaction - Transaction to use
     * @return String representation of the Key from Google App Engine.
     * This is the key that can be passed as the <code>keyString</code> parameter
     * in the future
     * @throws CentaurException
     */
    public <T> String save(T object, Transaction transaction) throws CentaurException;

    /**
     * Saves and indexes the provided object, using the provided transaction.
     *
     * @param object      - Object to Save
     * @param transaction - Transaction to use
     * @return String representation of the Key from Google App Engine.
     * This is the key that can be passed as the <code>keyString</code> parameter
     * in the future
     * @throws CentaurException
     */
    public <T> String saveAndIndex(T object, Transaction transaction) throws CentaurException;

    /**
     * Saves and indexes the provided object
     *
     * @param object - Object to Save
     * @return String representation of the Key from Google App Engine.
     * This is the key that can be passed as the <code>keyString</code> parameter
     * in the future
     * @throws CentaurException
     */
    public <T> String saveAndIndex(T object) throws CentaurException;

    /**
     * Saves the provided object as a child object of the provided parent object.
     * Note: The <code>parent</code> object must be a managed object of the Data Store.
     *
     * @param parent - Parent Object
     * @param object - Object to Save
     * @return String representation of the Key from Google App Engine for the child object.
     * This is the key that can be passed as the <code>keyString</code> parameter
     * in the future
     * @throws CentaurException
     */
    public <T, X> String saveChild(X parent, T object) throws CentaurException;

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
     * @throws CentaurException
     */
    public <T, X> String saveChild(X parent, T object, Transaction transaction) throws CentaurException;

    /**
     * Retrieves an object using the String representation of its Google App Engine Key.
     *
     * @param keyString          - Google App Engine Key String
     * @param expectedReturnType - Return type of the result
     * @return - The requested object
     * @throws CentaurException
     */
    public <T> T getObject(String keyString, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Retrieves an object using the object's <code>kind</code> and <code>name</code>.
     *
     * @param kind               - <code>kind</code> of the desired object
     * @param name               - <code>name</code> of the desired object
     * @param expectedReturnType - Return type of the result
     * @return - The requested object
     * @throws CentaurException
     */
    public <T> T getObject(String kind, String name, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Retrieves an object using the object's <code>kind</code> and <code>id</code>.
     *
     * @param kind               - <code>kind</code> of the desired object
     * @param id                 - <code>id</code> of the desired object
     * @param expectedReturnType - Return type of the result
     * @return - The requested object
     * @throws CentaurException
     */
    public <T> T getObject(String kind, long id, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Find an object by <code>property name</code> and <code>value</code>
     *
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @return - The requested object
     * @throws CentaurException
     */
    public <T> T findObjectByProperty(String propertyName, Object value, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Convenience method for finding an object by <code>userId</code>.
     * This is the same as calling:
     * <p>
     * <code>findObjectByProperty("userId", value, expectedReturnType);</code>
     *
     * @param userId             - Name of the property to search on
     * @param expectedReturnType - Return type of the result
     * @return - The requested object
     * @throws CentaurException
     */
    public <T> T findObjectByUserId(String userId, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Delete the provided object
     *
     * @param object - Object to delete.
     * @throws CentaurException
     */
    public <T> void deleteObject(T object) throws CentaurException;

    /**
     * Delete the provided <code>object</code> using the provided transaction.
     *
     * @param object      - Object to delete.
     * @param transaction - Transaction to use
     * @throws CentaurException
     */
    public <T> void deleteObject(T object, Transaction transaction) throws CentaurException;

    /**
     * Convenience method to delete a desired object by using the String representation
     * of the object's Google Data Store Key
     *
     * @param keyString - Key of the object that is to be deleted
     * @throws CentaurException
     */
    public void deleteObject(String keyString) throws CentaurException;

    /**
     * Convenience method to delete a desired object by using the String representation
     * of the object's Google Data Store Key using the provided transaction
     *
     * @param transaction - Transaction to use
     * @throws CentaurException
     */
    public void deleteObject(String keyString, Transaction transaction) throws CentaurException;

    /**
     * Convenience method for finding an object by <code>kind</code> and <code>userId</code>.
     *
     * @param kind               - Kind of the object to be found
     * @param userId             - User Id of the object to be found
     * @param expectedReturnType - Return type of the result
     * @return - The requested object
     * @throws CentaurException
     */
    public <T> T findObjectByUserId(String kind, String userId, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Returns all children of the provided kind for the given parent.
     *
     * @param parent             - Parent object
     * @param expectedReturnType - Return type of the result
     * @return - The <code>List</code> of children
     * @throws CentaurException
     */
    public <T, X> List<T> getAllChildren(X parent, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Returns all children of the provided kind for the given parent.
     *
     * @param parent             - Parent object
     * @param expectedReturnType - Return type of the result
     * @param dataOptions        - <code>DataOptions</code> to apply
     * @return - The <code>List</code> of children
     * @throws CentaurException
     */
    public <T, X> List<T> getAllChildren(X parent, Class<T> expectedReturnType, DataOptions dataOptions) throws CentaurException;

    /**
     * Returns all children of the provided kind for the given parent.
     *
     * @param parent             - Parent object
     * @param expectedReturnType - Return type of the result
     * @param fetchOptions       - <code>FetchOptions</code> to apply
     * @param dataOptions        - <code>DataOptions</code> to apply
     * @return - The <code>List</code> of children
     * @throws CentaurException
     */
    public <T, X> List<T> getAllChildren(X parent, Class<T> expectedReturnType, FetchOptions fetchOptions, DataOptions dataOptions) throws CentaurException;

    /**
     * Returns all children of the provided kind for the given parent.
     *
     * @param kind               - Kind of object to be found
     * @param parent             - Parent object
     * @param expectedReturnType - Return type of the result
     * @return - The <code>List</code> of children
     * @throws CentaurException
     */
    public <T, X> List<T> getAllChildren(String kind, X parent, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Returns all children of the provided kind for the given parent.
     *
     * @param kind               - Kind of object to be found
     * @param parent             - Parent object
     * @param expectedReturnType - Return type of the result
     * @param fetchOptions       - <code>FetchOptions</code> to apply
     * @param dataOptions        - <code>DataOptions</code> to apply
     * @return - The <code>List</code> of children
     * @throws CentaurException
     */
    public <T, X> List<T> getAllChildren(String kind, X parent, Class<T> expectedReturnType, FetchOptions fetchOptions, DataOptions dataOptions) throws CentaurException;

    /**
     * Finds an object by the provided <code>kind</code>, <code>property</code>, <code>value</code>
     *
     * @param kind               - Kind of object to be found
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @return - The <code>List</code> of found objects
     * @throws CentaurException
     */
    public <T> T findObject(String kind, String propertyName, Object value, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Returns a <code>List</code> of objects of the provided <code>kind</code>.
     *
     * @param kind               - Kind of objects to be found
     * @param expectedReturnType - Return type of the result
     * @return - The <code>List</code> of found objects
     * @throws CentaurException
     */
    public <T> List<T> getObjects(String kind, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Returns a <code>List</code> of objects for the provided
     * <code>kind</code>, <code>propertyName</code>, <code>value</code>
     *
     * @param kind               - Kind of objects to be found
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @return - The <code>List</code> of found objects
     * @throws CentaurException
     */
    public <T> List<T> findObjects(String kind, String propertyName, Object value, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Returns a sorted <code>List</code> of objects for the provided
     * <code>kind</code>, <code>propertyName</code>, <code>value</code>
     *
     * @param kind               - Kind of objects to be found
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @param sortField          - Name of the property to be sorted on
     * @return - The <code>List</code> of found objects
     * @throws CentaurException
     */
    public <T> List<T> findObjects(String kind, String propertyName, Object value, Class<T> expectedReturnType, String sortField) throws CentaurException;

    /**
     * Returns a sorted <code>List</code> of objects for the provided
     * <code>kind</code>, <code>propertyName</code>, <code>value</code>
     *
     * @param kind               - Kind of objects to be found
     * @param propertyName       - Name of the property to search on
     * @param value              - Value of the property to match
     * @param expectedReturnType - Return type of the result
     * @param sortCriteria       - Sort Criteria to be applied
     * @return - The <code>List</code> of found objects
     * @throws CentaurException
     */
    public <T> List<T> findObjectsSorted(String kind, String propertyName, Object value, Class<T> expectedReturnType, SortCriteria... sortCriteria) throws CentaurException;

    /**
     * Returns a sorted <code>List</code> of objects for the provided
     * <code>kind</code>, <code>propertyName</code>, <code>value</code>
     *
     * @param kind               - Kind of objects to be found
     * @param keyValues          - <code>Map</code> where the key is the name of the property to search on, and the
     *                           value is the value that the property is to match
     * @param expectedReturnType - Return type of the result
     * @param sortCriteria       - Sort Criteria to be applied
     * @return - The <code>List</code> of found objects
     * @throws CentaurException
     */
    public <T> List<T> findObjectsByPropertiesSorted(String kind, Map<String, Object> keyValues, Class<T> expectedReturnType, SortCriteria... sortCriteria) throws CentaurException;

    /**
     * Returns a <code>List</code> of objects for the provided
     * <code>kind</code>, <code>propertyName</code>, <code>value</code>
     *
     * @param kind               - Kind of objects to be found
     * @param keyValues          - <code>Map</code> where the key is the name of the property to search on, and the
     *                           value is the value that the property is to match
     * @param expectedReturnType - Return type of the result
     * @return - The <code>List</code> of found objects
     * @throws CentaurException
     */
    public <T> List<T> findObjects(String kind, Map<String, Object> keyValues, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Returns a <code>List</code> of objects for the provided <code>expectedReturnType</code>
     *
     * @param expectedReturnType - Return type of the result
     * @return - The <code>List</code> of found objects
     * @throws CentaurException
     */
    public <T> List<T> getObjects(Class<T> expectedReturnType) throws CentaurException;

    /**
     * Returns a list of Objects that match the given String representations of their Google App Engine Key
     *
     * @param keyStrings         - String representation of a Google App Engine Key
     * @param expectedReturnType - Return type of the result
     * @return - The <code>List</code> of found objects
     * @throws CentaurException
     */
    public <T> Map<String, T> getObjectByKeyStrings(List<String> keyStrings, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Returns a list of Objects that match the given Google App Engine Keys
     *
     * @param keys               - String representation of a Google App Engine Key
     * @param expectedReturnType - Return type of the result
     * @return - The <code>List</code> of found objects
     * @throws CentaurException
     */
    public <T> Map<String, T> getObjectByKeys(List<Key> keys, Class<T> expectedReturnType) throws CentaurException;

    /**
     * Returns a sorted <code>List</code> of <code>Entity</code>s for the provided
     * <code>kind</code>, <code>filter</code>
     *
     * @param kind         - Kind of objects to be found
     * @param filter       - <code>filter</code> to be applied
     * @param sortCriteria - Sort Criteria to be applied
     * @param fetchOptions - <code>FetchOptions</code> to be applied
     * @return - The <code>List</code> of found objects
     * @throws CentaurException
     */
    public QueryResultList<Entity> findEntitiesByFilterSorted(String kind, Query.Filter filter, List<SortCriteria> sortCriteria, FetchOptions fetchOptions) throws CentaurException;

    /**
     * Returns a sorted <code>ResultList</code> of <code>Entity</code>s for the provided
     * <code>expectedReturnType</code>, <code>filter</code>
     *
     * @param expectedReturnType - Return type of the result
     * @param filter             - <code>filter</code> to be applied
     * @param sortCriteria       - Sort Criteria to be applied
     * @param fetchOptions       - <code>FetchOptions</code> to be applied
     * @return - The <code>ResultList</code> of found objects
     * @throws CentaurException
     */
    public <T> ResultList<T> findObjectsByFilterSorted(Class expectedReturnType, Query.Filter filter, List<SortCriteria> sortCriteria, FetchOptions fetchOptions) throws CentaurException;

    /**
     * Returns a <code>ResultList</code> of <code>Entity</code>s for the provided
     * <code>expectedReturnType</code>, <code>filter</code>
     *
     * @param expectedReturnType - Return type of the result
     * @param filter             - <code>filter</code> to be applied
     * @return - The <code>ResultList</code> of found objects
     * @throws CentaurException
     */
    public <T> ResultList<T> findObjectsByFilter(Class expectedReturnType, Query.Filter filter) throws CentaurException;

    public <T> void indexDocument(Document document, Class<T> expectedReturnType) throws CentaurException;

    public void indexDocument(Document document, String indexName) throws CentaurException;

    public <T> SearchResults<T> search(Class<T> expectedReturnType, com.google.appengine.api.search.Query query) throws CentaurException;

    /**
     * Creates and returns a <code>Transaction</code>
     *
     * @return a <code>Transaction</code>
     */
    public Transaction beginTransaction();

    /**
     * Creates and returns a Cross-Group<code>transaction</code>
     *
     * @return a <code>Transaction</code>
     */
    public Transaction beginCrossGroupTransaction();

    /**
     * Rolls back the provided <code>transaction</code>
     *
     * @param transaction - The <code>Transaction</code> to rollback
     */
    public void rollback(Transaction transaction);

    /**
     * Commits the provided <code>transaction</code>
     *
     * @param transaction - The <code>Transaction</code> to commit
     */
    public void commit(Transaction transaction);
}
