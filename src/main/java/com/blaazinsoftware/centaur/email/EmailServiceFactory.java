package com.blaazinsoftware.centaur.email;

/**
 * @author Randy May
 *         Date: 14-10-31
 */
public class EmailServiceFactory {
    public static EmailService getInstance(String fromAddress) {
        return new DefaultEmailServiceImpl(fromAddress);
    }
}
