package com.blaazinsoftware.centaur.search.exception;

import com.blaazinsoftware.centaur.exception.CentaurException;

/**
 * @author Randy May
 *         Date: 2016-01-10
 */
public class SearchException extends CentaurException {
    public SearchException(Exception e) {
        super(e);
    }
}
