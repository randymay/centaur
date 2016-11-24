package com.blaazinsoftware.centaur.cache.service;

/**
 * @author Randy May
 *         Date: 2016-01-08
 */
public interface CacheService {

    /**
     * Cache an entity
     *
     * @param key    - Cache key
     * @param entity - Entity to cache
     * @param <T>    - Entity type
     */
    <T> void cacheEntity(String key, T entity);

    /**
     * Remove item from cache at the key provided
     *
     * @param key - Key of entity to be removed from cache
     */
    void unCacheEntity(String key);

    /**
     * Retrieves an object from the cache using the provided Web-safe <code>String</code> key.
     *
     * @param keyString - Key to retrieve
     * @param <T>       - Entity type
     * @return - Entity from cache
     */
    <T> T getEntityFromCache(String keyString);
}
