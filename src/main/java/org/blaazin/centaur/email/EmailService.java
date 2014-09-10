package org.blaazin.centaur.email;

import org.blaazin.centaur.BlaazinGAEException;

public interface EmailService {
    public void sendEmail(String subject, String body, String... to) throws BlaazinGAEException;
}
