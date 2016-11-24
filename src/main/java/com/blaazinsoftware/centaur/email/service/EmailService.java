package com.blaazinsoftware.centaur.email.service;

import com.blaazinsoftware.centaur.email.EmailException;

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
     * @throws EmailException - Exception
     */
    void sendEmail(String subject, String body, String... to) throws EmailException;
}
