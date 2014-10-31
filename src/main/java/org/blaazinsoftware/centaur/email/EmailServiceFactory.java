package org.blaazinsoftware.centaur.email;

/**
 * @author Randy May
 *         Date: 14-10-31
 */
public class EmailServiceFactory {
    public static EmailService getInstance() {
        return EmailServiceFactory.getInstance(null);
    }

    public static EmailService getInstance(String fromAddress) {
        EmailServiceImpl impl = new EmailServiceImpl();
        impl.setFrom(fromAddress);

        return impl;
    }
}
