package com.blaazin.gae.data.dao;

import com.blaazin.gae.BlaazinGAEException;
import com.blaazin.gae.data.dto.SortCriteria;
import com.google.appengine.api.datastore.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GeneralDAO {

    private static final Logger log = Logger.getLogger(GeneralDAO.class);

    private DatastoreService getDatastoreService() {
        return DatastoreServiceFactory.getDatastoreService();
    }

    public Entity getByKey(Key key) throws BlaazinGAEException {
        try {
            return getDatastoreService().get(key);
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new BlaazinGAEException(e);
        }
    }

    public Key save(Entity entity) {
        return getDatastoreService().put(entity);
    }

    public void delete(Entity entity) {
        getDatastoreService().delete(entity.getKey());
    }

    public Key save(Transaction transaction, Entity entity) {
        return getDatastoreService().put(transaction, entity);
    }

    public void delete(Transaction transaction, Entity entity) {
        getDatastoreService().delete(transaction, entity.getKey());
    }

    public Entity refresh(Entity entity) throws BlaazinGAEException {
        try {
            return getDatastoreService().get(entity.getKey());
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new BlaazinGAEException(e);
        }
    }

    public Entity getSingleEntityByPropertyValue(String kind, String property, Object value) throws BlaazinGAEException {
        Query.Filter filter = new Query.FilterPredicate(property, Query.FilterOperator.EQUAL, value);

        Query query = new Query(kind).setFilter(filter);

        PreparedQuery pq = getDatastoreService().prepare(query);

        return pq.asSingleEntity();
    }

    public List<Entity> getChildren(String kind, Entity parent) throws BlaazinGAEException {
        return getChildren(kind, parent, FetchOptions.Builder.withDefaults());
    }

    public List<Entity> getChildren(String kind, Entity parent, FetchOptions fetchOptions) throws BlaazinGAEException {
        refresh(parent);
        Query query = new Query(kind, parent.getKey());
        PreparedQuery pq = getDatastoreService().prepare(query);

        return pq.asList(fetchOptions);
    }

    public List<Entity> getEntitiesByKind(String kind) throws BlaazinGAEException {
        return getEntitiesByPropertyValue(kind, null, null);
    }

    public List<Entity> getEntitiesByPropertyValue(String kind, String property, Object value) throws BlaazinGAEException {
        Map<String, Object> keyValues = new HashMap<>();
        keyValues.put(property, value);

        return getEntitiesByPropertyValues(kind, keyValues);
    }

    public List<Entity> getEntitiesByPropertyValues(String kind, Map<String, Object> keyValues) throws BlaazinGAEException {
        return getEntitiesByPropertyValuesSorted(kind, keyValues);
    }

    public List<Entity> getEntitiesByPropertyValuesSorted(String kind, Map<String, Object> keyValues, SortCriteria... sortCriteria) throws BlaazinGAEException {
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
        return this.getDatastoreService().beginTransaction();
    }

    public void rollbackTransaction(Transaction transaction) {
        transaction.rollback();
    }

    public void commitTransaction(Transaction transaction) {
        transaction.commit();
    }
}
