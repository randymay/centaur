package org.blaazinsoftware.centaur.email;

import org.blaazinsoftware.centaur.CentaurException;

public interface EmailService {
    public void sendEmail(String subject, String body, String... to) throws CentaurException;
}
