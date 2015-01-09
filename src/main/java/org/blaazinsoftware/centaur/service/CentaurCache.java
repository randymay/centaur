package org.blaazinsoftware.centaur.service;

import org.blaazinsoftware.centaur.CentaurException;

/**
 * @author Randy May
 *         Date: 15-01-09
 */
public interface CentaurCache {
    public <T> T getObjectFromCacheByKey(String keyString) throws CentaurException;
    public <T> T getObjectFromCache(Object object) throws CentaurException;
    public <T> void cacheObjectByKey(String keyString, T objectToCache) throws CentaurException;
    public <T> void cacheObject(Object object, T objectToCache) throws CentaurException;
}
