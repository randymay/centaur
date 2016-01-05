package com.blaazinsoftware.centaur.email;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Default implementation of <code>EmailService</code>
 *
 * @author Randy May
 */
class DefaultEmailServiceImpl implements EmailService {

    private String from;

    protected DefaultEmailServiceImpl(String from) {
        this.from = from;
    }

    @Override
    public void sendEmail(String subject, String body, String... to) throws EmailException {
        this.sendEmail(from, subject, body, to);
    }

    protected void sendEmail(String from, String subject, String body, String... to) throws EmailException {
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
            throw new EmailException(e);
        }
    }
}
