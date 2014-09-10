package org.blaazin.centaur.service.impl;

import org.blaazin.centaur.BlaazinGAEException;
import org.blaazin.centaur.data.dao.GeneralDAO;
import org.blaazin.centaur.data.dto.BlaazinEntity;
import org.blaazin.centaur.data.util.EntityTranslator;
import org.blaazin.centaur.service.GeneralService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GeneralServiceImpl implements GeneralService {

    @Autowired
    private GeneralDAO generalDAO;

    @Override
    public <T extends BlaazinEntity> Key save(T object) throws BlaazinGAEException {
        return save(object, null);
    }

    @Override
    public <T extends BlaazinEntity> Key save(T object, Transaction transaction) throws BlaazinGAEException {
        if (null == object.getAppEngineKey()) {
            initKey(object);
        }
        Key key = generalDAO.save(transaction, EntityTranslator.toEntity(object));
        if (null == object.getAppEngineKey()) {
            object.setAppEngineKey(key);
        }

        return key;
    }

    private <T extends BlaazinEntity> void initKey(T object) throws BlaazinGAEException {
        if (null == object.getAppEngineKey()) {
            Key key = createKey(object);
            object.setAppEngineKey(key);
        }
    }

    @Override
    public <T extends BlaazinEntity, X extends BlaazinEntity> Key saveChild(X parent, T object) throws BlaazinGAEException {
        Key childKey = this.saveChild(parent, object, null);
        if (null == object.getAppEngineKey()) {
            object.setAppEngineKey(childKey);
        }

        return childKey;
    }

    @Override
    public <T extends BlaazinEntity, X extends BlaazinEntity> Key saveChild(X parent, T object, Transaction transaction) throws BlaazinGAEException {
        if (null == parent || null == parent.getAppEngineKey()) {
            throw new BlaazinGAEException("Parent Entity is required");
        }

        if (null == object.getAppEngineKey()) {
            Key key = createKey(parent, object);
            object.setAppEngineKey(key);
            if (null == key) {
                return generalDAO.save(transaction, EntityTranslator.toEntity(object, parent.getAppEngineKey()));
            }
        }
        return generalDAO.save(transaction, EntityTranslator.toEntity(object));
    }

    @Override
    public <T extends BlaazinEntity> T getObject(Key key, Class<T> klass) throws BlaazinGAEException {
        return EntityTranslator.fromEntity(generalDAO.getByKey(key), klass);
    }

    @Override
    public <T extends BlaazinEntity> T getObject(String kind, String name, Class<T> klass) throws BlaazinGAEException {
        Key key = KeyFactory.createKey(kind, name);

        return EntityTranslator.fromEntity(generalDAO.getByKey(key), klass);
    }

    @Override
    public <T extends BlaazinEntity> T getObject(String kind, long id, Class<T> klass) throws BlaazinGAEException {
        Key key = KeyFactory.createKey(kind, id);

        return EntityTranslator.fromEntity(generalDAO.getByKey(key), klass);
    }

    @Override
    public <T extends BlaazinEntity> T getObject(String name, Class<T> klass) throws BlaazinGAEException {
        Key key = KeyFactory.createKey(klass.getSimpleName(), name);

        return EntityTranslator.fromEntity(generalDAO.getByKey(key), klass);
    }

    @Override
    public <T extends BlaazinEntity> T getObject(String propertyName, Object value, Class<T> klass) throws BlaazinGAEException {
        return this.getObject(klass.getSimpleName(), propertyName, value, klass);
    }

    @Override
    public <T extends BlaazinEntity> T getObjectByUserId(String userId, Class<T> klass) throws BlaazinGAEException {
        return getObject(klass.getSimpleName(), "userId", userId, klass);
    }

    @Override
    public <T extends BlaazinEntity> T getObjectByUserId(String kind, String userId, Class<T> klass) throws BlaazinGAEException {
        return getObject(kind, "userId", userId, klass);
    }

    @Override
    public <T extends BlaazinEntity> T getObject(String kind, String propertyName, Object value, Class<T> klass) throws BlaazinGAEException {
        Entity entity = generalDAO.getSingleEntityByPropertyValue(kind, propertyName, value);
        if (entity == null) {
            return null;
        }

        return EntityTranslator.fromEntity(entity, klass);
    }

    @Override
    public <T extends BlaazinEntity> void deleteObject(T object) throws BlaazinGAEException {
        generalDAO.delete(EntityTranslator.toEntity(object));
    }

    @Override
    public <T extends BlaazinEntity> Key createKey(T object) throws BlaazinGAEException {
        if (object != null && !StringUtils.isEmpty(object.getKind()) && !StringUtils.isEmpty(object.getName())) {
            return KeyFactory.createKey(object.getKind(), object.getName());
        }

        return null;
    }

    @Override
    public <T extends BlaazinEntity, X extends BlaazinEntity> Key createKey(X parent, T object) throws BlaazinGAEException {
        if (object != null && !StringUtils.isEmpty(object.getKind()) && !StringUtils.isEmpty(object.getName())) {
            Entity parentEntity = EntityTranslator.toEntity(parent);
            return KeyFactory.createKey(parentEntity.getKey(), object.getKind(), object.getName());
        }

        return null;
    }

    @Override
    public <T extends BlaazinEntity, X extends BlaazinEntity> List<T> getChildren(String kind, X parent, Class<T> klass) throws BlaazinGAEException {
        Entity parentEntity = EntityTranslator.toEntity(parent);
        List<Entity> entities = generalDAO.getChildren(kind, parentEntity);

        List<T> results = new ArrayList<>();
        if (null == entities) {
            return null;
        }

        for (Entity entity : entities) {
            T object = EntityTranslator.fromEntity(entity, klass);
            results.add(object);
        }

        return results;
    }

    @Override
    public <T extends BlaazinEntity> List<T> getObjects(String kind, Class<T> klass) throws BlaazinGAEException {
        List<T> entityList = new ArrayList<>();
        List<Entity> entities = generalDAO.getEntitiesByKind(kind);
        if (entities == null) {
            return null;
        }

        for (Entity entity : entities) {
            T object = EntityTranslator.fromEntity(entity, klass);
            entityList.add(object);
        }

        return entityList;
    }

    @Override
    public <T extends BlaazinEntity> List<T> getObjects(String kind, String propertyName, Object value, Class<T> klass) throws BlaazinGAEException {
        List<Entity> entities = generalDAO.getEntitiesByPropertyValue(kind, propertyName, value);
        return getObjectListFromEntities(klass, entities);
    }

    private <T extends BlaazinEntity> List<T> getObjectListFromEntities(Class<T> klass, List<Entity> entities) {
        if (entities == null) {
            return null;
        }

        List<T> objectList = new ArrayList<>();
        for (Entity entity : entities) {
            T object = EntityTranslator.fromEntity(entity, klass);
            objectList.add(object);
        }

        return objectList;
    }

    @Override
    public <T extends BlaazinEntity> List<T> getObjects(String kind, Map<String, Object> keyValues, Class<T> klass) throws BlaazinGAEException {
        List<Entity> entities = generalDAO.getEntitiesByPropertyValues(kind, keyValues);
        return getObjectListFromEntities(klass, entities);
    }

    public Transaction beginTransaction() {
        return generalDAO.beginTransaction();
    }

    public void rollback(Transaction transaction) {
        generalDAO.rollbackTransaction(transaction);
    }

    public void commit(Transaction transaction) {
        generalDAO.commitTransaction(transaction);
    }
}
