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
            create(object);
        } else {
            update(object);
        }
    }

    @Override
    public <T extends BlaazinEntity> void create(T object) throws BlaazinGAEException {
        if (null == object.getAppEngineKey()) {
            if (StringUtils.isEmpty(object.getName())) {
                object.setName(UUID.randomUUID().toString());
            }
            Key key = createKey(object);
            object.setAppEngineKey(key);
        }

        generalDAO.insert(EntityTranslator.toEntity(object));
    }

    @Override
    public <T extends BlaazinEntity, X extends BlaazinEntity> void create(X parent, T object) throws BlaazinGAEException {
        if (null == parent || null == parent.getAppEngineKey()) {
            throw new BlaazinGAEException("Parent Entity is required");
        }

        if (null == object.getAppEngineKey()) {
            Key key = createKey(parent, object);
            object.setAppEngineKey(key);
        }

        generalDAO.insert(EntityTranslator.toEntity(object));
    }

    @Override
    public <T extends BlaazinEntity> T getByKey(Key key, Class<T> klass) throws BlaazinGAEException {
        return EntityTranslator.fromEntity(generalDAO.getByKey(key), klass);
    }

    @Override
    public <T extends BlaazinEntity> T getObject(String kind, String name, Class<T> klass) throws BlaazinGAEException {
        Key key = KeyFactory.createKey(kind, name);

        return EntityTranslator.fromEntity(generalDAO.getByKey(key), klass);
    }

    @Override
    public <T extends BlaazinEntity> void update(T object) throws BlaazinGAEException {
        generalDAO.update(EntityTranslator.toEntity(object));
    }

    @Override
    public <T extends BlaazinEntity> void delete(T object) throws BlaazinGAEException {
        generalDAO.delete(EntityTranslator.toEntity(object));
    }

    @Override
    public <T extends BlaazinEntity> T getSingleObjectByUserId(String kind, String userId, Class<T> klass) throws BlaazinGAEException {
        final Entity entity = generalDAO.getSingleEntityByUserId(kind, userId);
        return EntityTranslator.fromEntity(entity, klass);
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
    public <T extends BlaazinEntity> T getSingleObjectByPropertyValue(String kind, String property, Object value, Class<T> klass) throws BlaazinGAEException {
        Entity entity = generalDAO.getSingleEntityByPropertyValue(kind, property, value);
        if (entity == null) {
            return null;
        }

        return EntityTranslator.fromEntity(entity, klass);
    }

    @Override
    public <T extends BlaazinEntity> List<T> getObjectsByKind(String kind, Class<T> klass) throws BlaazinGAEException {
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
    public <T extends BlaazinEntity> List<T> getObjectsByPropertyValue(String kind, String property, Object value, Class<T> klass) throws BlaazinGAEException {
        List<Entity> entities = generalDAO.getEntitiesByPropertyValue(kind, property, value);
        return getObjectsFromEntities(klass, entities);
    }

    private <T extends BlaazinEntity> List<T> getObjectsFromEntities(Class<T> klass, List<Entity> entities) {
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
    public <T extends BlaazinEntity> List<T> getObjectsByPropertyValues(String kind, Map<String, Object> keyValues, Class<T> klass) throws BlaazinGAEException {
        List<Entity> entities = generalDAO.getEntitiesByPropertyValues(kind, keyValues);
        return getObjectsFromEntities(klass, entities);
    }
}
