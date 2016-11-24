package com.blaazinsoftware.centaur.cache.exception;

import com.blaazinsoftware.centaur.exception.CentaurException;

/**
 * @author Randy May
 *         Date: 2016-01-10
 */
public class CacheException extends CentaurException {
    public CacheException(Exception e) {
        super(e);
    }
}
