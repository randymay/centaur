package org.blaazinsoftware.centaur.service;

import org.blaazinsoftware.centaur.CentaurException;
import org.blaazinsoftware.centaur.data.dto.SortCriteria;
import com.google.appengine.api.datastore.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Randy May <a href="www.blaazinsoftware.com">Blaazin Software Consulting, Inc.</a>
 */
class DefaultCentaurDAO implements CentaurDAO {

    private static final Logger log = LoggerFactory.getLogger(DefaultCentaurDAO.class);

    private DatastoreService getDatastoreService() {
        return DatastoreServiceFactory.getDatastoreService();
    }

    public Entity getByKey(Key key) throws CentaurException {
        try {
            return getDatastoreService().get(key);
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new CentaurException(e);
        }
    }

    public Key save(Transaction transaction, Entity entity) {
        return getDatastoreService().put(transaction, entity);
    }

    public void delete(Transaction transaction, Entity entity) {
        getDatastoreService().delete(transaction, entity.getKey());
    }

    public Entity refresh(Entity entity) throws CentaurException {
        try {
            return getDatastoreService().get(entity.getKey());
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new CentaurException(e);
        }
    }

    public Entity getSingleEntityByPropertyValue(String kind, String property, Object value) throws CentaurException {
        Query.Filter filter = new Query.FilterPredicate(property, Query.FilterOperator.EQUAL, value);

        Query query = new Query(kind).setFilter(filter);

        PreparedQuery pq = getDatastoreService().prepare(query);

        return pq.asSingleEntity();
    }

    public List<Entity> getChildren(String kind, Entity parent) throws CentaurException {
        return getChildren(kind, parent, FetchOptions.Builder.withDefaults());
    }

    public List<Entity> getChildren(String kind, Entity parent, FetchOptions fetchOptions) throws CentaurException {
        refresh(parent);
        Query query = new Query(kind, parent.getKey());
        PreparedQuery pq = getDatastoreService().prepare(query);

        return pq.asList(fetchOptions);
    }

    public List<Entity> getEntitiesByKind(String kind) throws CentaurException {
        return getEntitiesByPropertyValue(kind, null, null);
    }

    public List<Entity> getEntitiesByPropertyValue(String kind, String property, Object value) throws CentaurException {
        Map<String, Object> keyValues = new HashMap<>();
        keyValues.put(property, value);

        return getEntitiesByPropertyValues(kind, keyValues);
    }

    public List<Entity> getEntitiesByPropertyValues(String kind, Map<String, Object> keyValues) throws CentaurException {
        return getEntitiesByPropertyValuesSorted(kind, keyValues);
    }

    public List<Entity> getEntitiesByPropertyValuesSorted(String kind, Map<String, Object> keyValues, SortCriteria... sortCriteria) throws CentaurException {
        Query query = new Query(kind);

        if (keyValues != null && !keyValues.isEmpty()) {
            List<Query.Filter> filters = new ArrayList<>();
            for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
                String property = entry.getKey();
                Object value = entry.getValue();

                filters.add(new Query.FilterPredicate(property, Query.FilterOperator.EQUAL, value));
            }

            Query.Filter filter;
            if (filters.size() == 1) {
                filter = filters.get(0);
            } else {
                filter = new Query.CompositeFilter(Query.CompositeFilterOperator.AND, filters);
            }
            query.setFilter(filter);
        }

        if (sortCriteria != null) {
           for (SortCriteria criteria : sortCriteria) {
               if (!StringUtils.isEmpty(criteria.getPropertyName())) {
                   Query.SortDirection direction = Query.SortDirection.DESCENDING;
                   if (criteria.isAscending()) {
                       direction = Query.SortDirection.ASCENDING;
                   }
                   query.addSort(criteria.getPropertyName(), direction);
               }
           }
        }

        PreparedQuery pq = getDatastoreService().prepare(query);

        return pq.asList(FetchOptions.Builder.withDefaults());
    }

    public Transaction beginTransaction() {
        TransactionOptions options = TransactionOptions.Builder.withDefaults();
        return this.beginTransaction(options);
    }

    public Transaction beginCrossGroupTransaction() {
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        return this.beginTransaction(options);
    }

    public Transaction beginTransaction(TransactionOptions options) {
        return this.getDatastoreService().beginTransaction(options);
    }

    public void rollbackTransaction(Transaction transaction) {
        transaction.rollback();
    }

    public void commitTransaction(Transaction transaction) {
        transaction.commit();
    }
}
