package com.blaazinsoftware.centaur.email;

import com.blaazinsoftware.centaur.exception.CentaurException;

/**
 * @author Randy May
 *         Date: 15-09-28
 */
public class EmailException extends CentaurException {
    public EmailException(Exception e) {
        super(e);
    }
}
