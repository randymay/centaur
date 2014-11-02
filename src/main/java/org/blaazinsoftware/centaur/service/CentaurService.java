package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.*;
import org.blaazinsoftware.centaur.CentaurException;
import org.blaazinsoftware.centaur.data.dto.SortCriteria;

import java.util.List;
import java.util.Map;

/**
 * @author Randy May <a href="www.blaazinsoftware.com">Blaazin Software Consulting, Inc.</a>
 */
public interface CentaurService {

    public <T> String save(T object) throws CentaurException;

    public <T> String save(T object, Transaction transaction) throws CentaurException;

    public <T, X> String saveChild(X parent, T object) throws CentaurException;

    public <T, X> String saveChild(X parent, T object, Transaction transaction) throws CentaurException;

    public <T> T getObject(String keyString, Class<T> klass) throws CentaurException;

    public <T> T getObject(String kind, String name, Class<T> klass) throws CentaurException;

    public <T> T getObject(String kind, long id, Class<T> klass) throws CentaurException;

    public <T> T getObjectByProperty(String propertyName, Object value, Class<T> klass) throws CentaurException;

    public <T> T getObjectByUserId(String userId, Class<T> klass) throws CentaurException;

    public <T> void deleteObject(T object) throws CentaurException;

    public <T> void deleteObject(T object, Transaction transaction) throws CentaurException;

    public void deleteObject(String keyString) throws CentaurException;

    public void deleteObject(String keyString, Transaction transaction) throws CentaurException;

    public <T> T getObjectByUserId(String kind, String userId, Class<T> klass) throws CentaurException;

    public <T, X> List<T> getChildren(String kind, X parent, Class<T> klass) throws CentaurException;

    public <T> T getObject(String kind, String property, Object value, Class<T> klass) throws CentaurException;

    public <T> List<T> getObjects(String kind, Class<T> klass) throws CentaurException;

    public <T> List<T> getObjects(String kind, String propertyName, Object value, Class<T> klass) throws CentaurException;

    public <T> List<T> getObjects(String kind, Map<String, Object> keyValues, Class<T> klass) throws CentaurException;

    public <T> List<T> getObjects(Class<T> klass) throws CentaurException;

    public <T> Map<String, T> getByKeyStrings(List<String> keyStrings, Class<T> klass) throws CentaurException;

    public <T> Map<String, T> getByKeys(List<Key> keys, Class<T> klass) throws CentaurException;

    public QueryResultList<Entity> getEntitiesByFilterSorted(String kind, Query.Filter filter, List<SortCriteria> sortCriteria, FetchOptions fetchOptions) throws CentaurException;

    public <T> ResultList<T> getObjectsByFilterSorted(Class klass, Query.Filter filter, List<SortCriteria> sortCriteria, FetchOptions fetchOptions) throws CentaurException;

    public <T> ResultList<T> getObjectsByFilter(Class klass, Query.Filter filter) throws CentaurException;

    public Transaction beginTransaction();

    public Transaction beginCrossGroupTransaction();

    public void rollback(Transaction transaction);

    public void commit(Transaction transaction);
}
