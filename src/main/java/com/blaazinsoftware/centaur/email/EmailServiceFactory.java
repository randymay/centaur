package com.blaazinsoftware.centaur.email;

/**
 * @author Randy May
 *         Date: 14-10-31
 */
public class EmailServiceFactory {
    public static EmailService getInstance() {
        return EmailServiceFactory.getInstance(null);
    }

    public static EmailService getInstance(String fromAddress) {
        DefaultEmailServiceImpl impl = new DefaultEmailServiceImpl();
        impl.setFrom(fromAddress);

        return impl;
    }
}
