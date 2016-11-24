package com.blaazinsoftware.centaur.cache.service;

import com.blaazinsoftware.centaur.dao.BasicDAO;

/**
 * @author Randy May
 *         Date: 2016-01-08
 */
public class DefaultCacheServiceImpl implements CacheService {
    private BasicDAO dao = new BasicDAO();

    protected DefaultCacheServiceImpl() {}

    @Override
    public <T> void cacheEntity(String key, T entity) {
        dao.cacheEntity(key, entity);
    }

    @Override
    public void unCacheEntity(String key) {
        dao.unCacheEntity(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getEntityFromCache(String keyString) {
        return (T) dao.loadFromCache(keyString);
    }
}
