package com.blaazinsoftware.centaur.data.exception;

import com.blaazinsoftware.centaur.exception.CentaurException;

/**
 * @author Randy May
 *         Date: 2016-01-10
 */
public class DataException extends CentaurException {
    public DataException(Exception e) {
        super(e);
    }
}
