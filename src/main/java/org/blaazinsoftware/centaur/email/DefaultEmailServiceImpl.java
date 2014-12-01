package org.blaazinsoftware.centaur.email;

import org.blaazinsoftware.centaur.CentaurException;

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

    @Override
    public void sendEmail(String subject, String body, String... to) throws CentaurException {
        this.sendEmail(from, subject, body, to);
    }

    @Override
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

    public void setFrom(String from) {
        this.from = from;
    }
}
