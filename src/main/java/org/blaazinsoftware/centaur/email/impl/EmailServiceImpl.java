package org.blaazinsoftware.centaur.email.impl;

import org.blaazinsoftware.centaur.CentaurException;
import org.blaazinsoftware.centaur.email.EmailService;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailServiceImpl implements EmailService {

    private String from;

    public void sendEmail(String subject, String body, String... to) throws CentaurException {
        this.sendEmail(from, subject, body, to);
    }

    public void sendEmail(String from, String subject, String body, String... to) throws CentaurException {
        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            for (String target : to) {
                msg.addRecipient(Message.RecipientType.TO,
                        new InternetAddress(target));
            }
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);
        } catch (Exception e) {
            throw new CentaurException(e);
        }
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
