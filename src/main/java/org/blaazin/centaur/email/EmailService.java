package org.blaazin.centaur.email;

import org.blaazin.centaur.CentaurException;

public interface EmailService {
    public void sendEmail(String subject, String body, String... to) throws CentaurException;
}
