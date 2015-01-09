package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.blaazinsoftware.centaur.CentaurException;
import org.blaazinsoftware.centaur.data.dto.SortCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Default implementation of <code>CentaurDAO</code>.
 *
 * @author Randy May
 */
class DefaultCentaurDAOImpl implements CentaurDAO {

    private static final Logger log = LoggerFactory.getLogger(DefaultCentaurDAOImpl.class);

    private DatastoreService getDatastoreService() {
        return DatastoreServiceFactory.getDatastoreService();
    }

    @Override
    public Entity getByKey(Key key) throws CentaurException {
        try {
            return getDatastoreService().get(key);
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new CentaurException(e);
        }
    }

    @Override
    public Map<Key, Entity> getByKeys(List<Key> keys) throws CentaurException {
        return getDatastoreService().get(keys);
    }

    @Override
    public Key save(Transaction transaction, Entity entity) {
        return getDatastoreService().put(transaction, entity);
    }

    @Override
    public void delete(Transaction transaction, Entity entity) {
        getDatastoreService().delete(transaction, entity.getKey());
    }

    @Override
    public Entity refresh(Entity entity) throws CentaurException {
        try {
            return getDatastoreService().get(entity.getKey());
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new CentaurException(e);
        }
    }

    @Override
    public Entity findSingleEntityByPropertyValue(String kind, String propertyName, Object value) throws CentaurException {
        Query.Filter filter = new Query.FilterPredicate(propertyName, Query.FilterOperator.EQUAL, value);

        Query query = new Query(kind).setFilter(filter);

        PreparedQuery pq = getDatastoreService().prepare(query);

        return pq.asSingleEntity();
    }

    @Override
    public List<Entity> getAllChildren(String kind, Entity parent) throws CentaurException {
        return getAllChildren(kind, parent, FetchOptions.Builder.withDefaults());
    }

    @Override
    public List<Entity> getAllChildren(String kind, Entity parent, FetchOptions fetchOptions) throws CentaurException {
        refresh(parent);
        Query query = new Query(kind, parent.getKey());
        PreparedQuery pq = getDatastoreService().prepare(query);

        return pq.asList(fetchOptions);
    }

    @Override
    public List<Entity> getAllEntitiesByKind(String kind) throws CentaurException {
        return findEntitiesByPropertyValue(kind, null, null);
    }

    @Override
    public List<Entity> findEntitiesByPropertyValue(String kind, String propertyName, Object value) throws CentaurException {
        Map<String, Object> keyValues = new HashMap<>();
        if (null != propertyName) {
            keyValues.put(propertyName, value);
        }

        return findEntitiesByPropertyValues(kind, keyValues);
    }

    @Override
    public List<Entity> findEntitiesByPropertyValue(String kind, String propertyName, Object value, SortCriteria... sortCriteria) throws CentaurException {
        Map<String, Object> keyValues = new HashMap<>();
        keyValues.put(propertyName, value);

        return findEntitiesByPropertyValuesSorted(kind, keyValues, sortCriteria);
    }

    @Override
    public List<Entity> findEntitiesByPropertyValues(String kind, Map<String, Object> keyValues) throws CentaurException {
        return findEntitiesByPropertyValuesSorted(kind, keyValues);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Entity> findEntitiesByPropertyValuesSorted(String kind, Map<String, Object> keyValues, SortCriteria... sortCriteria) throws CentaurException {
        Query query = new Query(kind);

        Query.Filter filter = null;
        if (keyValues != null && !keyValues.isEmpty()) {
            List<Query.Filter> filters = new ArrayList<>();
            for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
                String property = entry.getKey();
                Object value = entry.getValue();

                filters.add(new Query.FilterPredicate(property, Query.FilterOperator.EQUAL, value));
            }

            if (filters.size() == 1) {
                filter = filters.get(0);
            } else {
                filter = new Query.CompositeFilter(Query.CompositeFilterOperator.AND, filters);
            }
            query.setFilter(filter);
        }

        List<SortCriteria> sortCriteriaList = null;
        if (sortCriteria != null) {
            sortCriteriaList = Arrays.asList(sortCriteria);
        }

        return findEntitiesByFilterSorted(kind, filter, sortCriteriaList, FetchOptions.Builder.withDefaults());
    }

    @Override
    public QueryResultList<Entity> findEntitiesByFilterSorted(String kind, Query.Filter filter, List<SortCriteria> sortCriteria, FetchOptions fetchOptions) throws CentaurException {
        Query query = new Query(kind);

        if (filter != null) {
            query.setFilter(filter);
        }

        if (CollectionUtils.isNotEmpty(sortCriteria)) {
            for (SortCriteria criteria : sortCriteria) {
                if (criteria != null && !StringUtils.isEmpty(criteria.getPropertyName())) {
                    Query.SortDirection direction = Query.SortDirection.DESCENDING;
                    if (criteria.isAscending()) {
                        direction = Query.SortDirection.ASCENDING;
                    }
                    query.addSort(criteria.getPropertyName(), direction);
                }
            }
        }

        PreparedQuery pq = getDatastoreService().prepare(query);

        if (fetchOptions == null) {
            fetchOptions = FetchOptions.Builder.withDefaults();
        }
        return pq.asQueryResultList(fetchOptions);
    }

    @Override
    public Transaction beginTransaction() {
        TransactionOptions options = TransactionOptions.Builder.withDefaults();
        return this.beginTransaction(options);
    }

    @Override
    public Transaction beginCrossGroupTransaction() {
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        return this.beginTransaction(options);
    }

    public Transaction beginTransaction(TransactionOptions options) {
        return this.getDatastoreService().beginTransaction(options);
    }

    @Override
    public void rollbackTransaction(Transaction transaction) {
        transaction.rollback();
    }

    @Override
    public void commitTransaction(Transaction transaction) {
        transaction.commit();
    }
}
