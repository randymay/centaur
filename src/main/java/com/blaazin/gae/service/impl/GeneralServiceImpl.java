package com.blaazin.gae.service.impl;

import com.blaazin.gae.BlaazinGAEException;
import com.blaazin.gae.data.dao.GeneralDAO;
import com.blaazin.gae.data.dto.BlaazinEntity;
import com.blaazin.gae.data.util.EntityTranslator;
import com.blaazin.gae.service.GeneralService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GeneralServiceImpl implements GeneralService {

    @Autowired
    private GeneralDAO generalDAO;

    @Override
    public <T extends BlaazinEntity> void save(T object) throws BlaazinGAEException {
        if (null == object.getAppEngineKey()) {
            initKey(object);
        }
        generalDAO.save(EntityTranslator.toEntity(object));
    }

    private <T extends BlaazinEntity> void initKey(T object) throws BlaazinGAEException {
        if (null == object.getAppEngineKey()) {
            if (StringUtils.isEmpty(object.getName())) {
                object.setName(UUID.randomUUID().toString());
            }
            Key key = createKey(object);
            object.setAppEngineKey(key);
        }
    }

    @Override
    public <T extends BlaazinEntity, X extends BlaazinEntity> void save(X parent, T object) throws BlaazinGAEException {
        if (null == parent || null == parent.getAppEngineKey()) {
            throw new BlaazinGAEException("Parent Entity is required");
        }

        if (null == object.getAppEngineKey()) {
            Key key = createKey(parent, object);
            object.setAppEngineKey(key);
        }

        generalDAO.save(EntityTranslator.toEntity(object));
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
        return KeyFactory.createKey(object.getKind(), object.getName());
    }

    @Override
    public <T extends BlaazinEntity, X extends BlaazinEntity> Key createKey(X parent, T object) throws BlaazinGAEException {
        Entity parentEntity = EntityTranslator.toEntity(parent);
        return KeyFactory.createKey(parentEntity.getKey(), object.getKind(), object.getName());
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
}
