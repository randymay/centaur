package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.*;
import org.blaazinsoftware.centaur.CentaurException;
import org.blaazinsoftware.centaur.data.dto.SortCriteria;

import java.util.List;
import java.util.Map;

/**
 * @author Randy May <a href="www.blaazinsoftware.com">Blaazin Software Consulting, Inc.</a>
 */
public interface CentaurDAO {

    public Entity getByKey(Key key) throws CentaurException;

    public Map<Key, Entity> getByKeys(List<Key> keys) throws CentaurException;

    public Key save(Transaction transaction, Entity entity);

    public void delete(Transaction transaction, Entity entity);

    public Entity refresh(Entity entity) throws CentaurException;

    public Entity getSingleEntityByPropertyValue(String kind, String property, Object value) throws CentaurException;

    public List<Entity> getChildren(String kind, Entity parent) throws CentaurException;

    public List<Entity> getChildren(String kind, Entity parent, FetchOptions fetchOptions) throws CentaurException;

    public List<Entity> getEntitiesByKind(String kind) throws CentaurException;

    public List<Entity> getEntitiesByPropertyValue(String kind, String property, Object value) throws CentaurException;

    public List<Entity> getEntitiesByPropertyValue(String kind, String property, Object value, SortCriteria... sortCriteria) throws CentaurException;

    public List<Entity> getEntitiesByPropertyValues(String kind, Map<String, Object> keyValues) throws CentaurException;

    public List<Entity> getEntitiesByPropertyValuesSorted(String kind, Map<String, Object> keyValues, SortCriteria... sortCriteria) throws CentaurException;

    public QueryResultList<Entity> getEntitiesByFilterSorted(String kind, Query.Filter filter, List<SortCriteria> sortCriteria, FetchOptions fetchOptions) throws CentaurException;

    public Transaction beginTransaction();

    public Transaction beginCrossGroupTransaction();

    public void rollbackTransaction(Transaction transaction);

    public void commitTransaction(Transaction transaction);
}
