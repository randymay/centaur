package com.blaazin.gae.email;

import com.blaazin.gae.BlaazinGAEException;

public interface EmailService {
    public void sendEmail(String subject, String body, String... to) throws BlaazinGAEException;
}
