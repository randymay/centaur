package com.blaazinsoftware.centaur.cache.service;

/**
 * @author Randy May
 *         Date: 2016-01-08
 */
public class CacheServiceFactory {
    public static CacheService getInstance() {
        return new DefaultCacheServiceImpl();
    }
}
