package org.blaazinsoftware.centaur.email;

import org.blaazinsoftware.centaur.CentaurException;

/**
 * Service to send E-mail messages through Google App Engine
 */
public interface EmailService {

    /**
     * Sends an E-mail message using Google App Engine
     *
     * @param subject               - Subject of E-mail
     * @param body                  - Body of E-mail
     * @param to                    - E-mail addresses of recipients
     *
     * @throws CentaurException
     */
    public void sendEmail(String subject, String body, String... to) throws CentaurException;

    /**
     * Sends an E-mail message using Google App Engine
     *
     * @param from                  - E-mail address of the sender
     * @param subject               - Subject of E-mail
     * @param body                  - Body of E-mail
     * @param to                    - E-mail addresses of recipients
     *
     * @throws CentaurException
     */
    public void sendEmail(String from, String subject, String body, String... to) throws CentaurException;
}
