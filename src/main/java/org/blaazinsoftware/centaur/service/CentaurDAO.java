package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.*;
import org.blaazinsoftware.centaur.CentaurException;
import org.blaazinsoftware.centaur.query.DataOptions;
import org.blaazinsoftware.centaur.search.SortCriteria;

import java.util.List;
import java.util.Map;

/**
 * @author Randy May
 */
public interface CentaurDAO {

    /**
     * Retrieves an entity by it's <code>Key</code>
     *
     * @param key - Key of the Entity to return
     * @return - The desired Entity
     * @throws CentaurException
     */
    public Entity getByKey(Key key) throws CentaurException;

    /**
     * Retrieves an entity by it's <code>Key</code>
     *
     * @param keys - <code>List</code> of <code>Key</code>s of the Entities to return
     * @return - A <code>List</code> of desired Entities
     * @throws CentaurException
     */
    public Map<Key, Entity> getByKeys(List<Key> keys) throws CentaurException;

    /**
     * Saves the provided <code>Entity</code> using the provided <code>Transaction</code>
     *
     * @param transaction - The <code>Transaction</code> to use, if <code>null</code>, do not use a transaction
     * @param entity      - The <code>Entity</code> to save.
     * @return - The <code>Key</code> of the saved <code>Entity</code>
     */
    public Key save(Transaction transaction, Entity entity);

    /**
     * Deletes the provided <code>Entity</code> using the provided <code>Transaction</code>
     *
     * @param transaction - The <code>Transaction</code> to use, if <code>null</code>, do not use a transaction
     * @param entity      - The <code>Entity</code> to delete.
     */
    public void delete(Transaction transaction, Entity entity);

    /**
     * Refreshed the provided <code>Entity</code>
     *
     * @param entity - The <code>Entity</code> to refresh.
     * @return - The refreshed <code>Entity</code>
     */
    public Entity refresh(Entity entity) throws CentaurException;

    /**
     * Find an <code>Entity</code> by <code>propertyName</code> and <code>value</code>
     *
     * @param kind         - Kind of object to be found
     * @param propertyName - Name of the property to search on
     * @param value        - Value of the property to match
     * @return - The requested <code>Entity</code>
     * @throws CentaurException
     */
    public Entity findSingleEntityByPropertyValue(String kind, String propertyName, Object value) throws CentaurException;

    /**
     * Returns all children of the provided <code>kind</code> for the given <code>parent</code>.
     *
     * @param kind   - Kind of object to be found
     * @param parent - Parent object
     * @return - The <code>List</code> of children
     * @throws CentaurException
     */
    public List<Entity> getAllChildren(String kind, Entity parent) throws CentaurException;

    /**
     * Returns all children of the provided <code>parent</code>.
     *
     * @param parent - Parent object
     * @return - The <code>List</code> of children
     * @throws CentaurException
     */
    public List<Entity> getAllChildren(Entity parent, FetchOptions fetchOptions) throws CentaurException;

    /**
     * Returns all children of the provided <code>kind</code> for the given <code>parent</code>.
     *
     * @param kind         - Kind of object to be found
     * @param parent       - Parent object
     * @param fetchOptions - <code>FetchOptions</code> to apply
     * @return - The <code>List</code> of children
     * @throws CentaurException
     */
    public List<Entity> getAllChildren(String kind, Entity parent, FetchOptions fetchOptions) throws CentaurException;

    /**
     * Returns all children of the provided <code>kind</code> for the given <code>parent</code>.
     *
     * @param kind         - Kind of object to be found
     * @param parent       - Parent object
     * @param fetchOptions - <code>FetchOptions</code> to apply
     * @param dataOptions  - <code>DataOptions</code> to apply
     * @return - The <code>List</code> of children
     * @throws CentaurException
     */
    public List<Entity> getAllChildren(String kind, Entity parent, FetchOptions fetchOptions, DataOptions dataOptions) throws CentaurException;

    /**
     * Returns all <code>Entities</code> of the provided <code>kind</code>.
     *
     * @param kind - Kind of object to be found
     * @return - The <code>List</code> of Entities
     * @throws CentaurException
     */
    public List<Entity> getAllEntitiesByKind(String kind) throws CentaurException;

    /**
     * Returns a <code>List</code> of Entities for the provided
     * <code>kind</code>, <code>propertyName</code>, <code>value</code>
     *
     * @param kind         - Kind of objects to be found
     * @param propertyName - Name of the property to search on
     * @param value        - Value of the property to match
     * @return - The <code>List</code> of found Entities
     * @throws CentaurException
     */
    public List<Entity> findEntitiesByPropertyValue(String kind, String propertyName, Object value) throws CentaurException;

    /**
     * Returns a sorted <code>List</code> of Entities for the provided
     * <code>kind</code>, <code>propertyName</code>, <code>value</code>
     *
     * @param kind         - Kind of objects to be found
     * @param propertyName - Name of the property to search on
     * @param value        - Value of the property to match
     * @param sortCriteria - <code>SortCriteria</code> to apply
     * @return - The <code>List</code> of found Entities
     * @throws CentaurException
     */
    public List<Entity> findEntitiesByPropertyValue(String kind, String propertyName, Object value, SortCriteria... sortCriteria) throws CentaurException;

    /**
     * Returns a <code>List</code> of Entities for the provided
     * <code>kind</code>, <code>propertyName</code>, <code>value</code>
     *
     * @param kind      - Kind of objects to be found
     * @param keyValues - <code>Map</code> where the key is the name of the property to search on, and the
     *                  value is the value that the property is to match
     * @return - The <code>List</code> of found Entities
     * @throws CentaurException
     */
    public List<Entity> findEntitiesByPropertyValues(String kind, Map<String, Object> keyValues) throws CentaurException;

    /**
     * Returns a <code>List</code> of Entities for the provided
     * <code>kind</code>, <code>propertyName</code>, <code>value</code>
     *
     * @param kind         - Kind of objects to be found
     * @param keyValues    - <code>Map</code> where the key is the name of the property to search on, and the
     *                     value is the value that the property is to match
     * @param sortCriteria - <code>SortCriteria</code> to apply
     * @return - The <code>List</code> of found Entities
     * @throws CentaurException
     */
    public List<Entity> findEntitiesByPropertyValuesSorted(String kind, Map<String, Object> keyValues, SortCriteria... sortCriteria) throws CentaurException;

    /**
     * Returns a <code>QueryResultList</code> of Entities for the provided
     * <code>kind</code>, <code>filter</code>, <code>fetchOptions</code>
     *
     * @param kind         - Kind of objects to be found
     * @param filter       - <code>Filter</code> to be applied
     * @param sortCriteria - <code>SortCriteria</code> to apply
     * @param fetchOptions - <code>FetchOptions</code> to apply
     * @return - The <code>List</code> of found Entities
     * @throws CentaurException
     */
    public QueryResultList<Entity> findEntitiesByFilterSorted(String kind, Query.Filter filter, List<SortCriteria> sortCriteria, FetchOptions fetchOptions) throws CentaurException;

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
    public void rollbackTransaction(Transaction transaction);

    /**
     * Commits the provided <code>transaction</code>
     *
     * @param transaction - The <code>Transaction</code> to commit
     */
    public void commitTransaction(Transaction transaction);
}
