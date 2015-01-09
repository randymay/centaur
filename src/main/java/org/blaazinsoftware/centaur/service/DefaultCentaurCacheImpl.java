package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import org.blaazinsoftware.centaur.CentaurException;

/**
 * @author Randy May
 *         Date: 15-01-09
 */
public class DefaultCentaurCacheImpl implements CentaurCache {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getObjectFromCacheByKey(String keyString) throws CentaurException {
        return (T)getObjectFromCache(keyString);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getObjectFromCache(Object object) throws CentaurException {
        return (T)getMemcacheService().get(object);
    }

    @Override
    public <T> void cacheObjectByKey(String keyString, T objectToCache) throws CentaurException {
        cacheObject(keyString, objectToCache);
    }

    @Override
    public <T> void cacheObject(Object object, T objectToCache) throws CentaurException {
        getMemcacheService().put(object, objectToCache);
    }

    private MemcacheService getMemcacheService() {
        return MemcacheServiceFactory.getMemcacheService();
    }
}
