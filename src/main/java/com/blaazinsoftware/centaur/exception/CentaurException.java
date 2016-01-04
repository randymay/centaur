package com.blaazinsoftware.centaur.exception;

public class CentaurException extends Exception {
    public CentaurException() {

    }

    public CentaurException(String message) {
        super(message);
    }

    public CentaurException(Exception e) {
        super(e);
    }
}
