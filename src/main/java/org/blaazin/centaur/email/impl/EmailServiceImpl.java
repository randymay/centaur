package org.blaazin.centaur.email.impl;

import org.blaazin.centaur.CentaurException;
import org.blaazin.centaur.email.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("mail.from")
    private String from;

    public void sendEmail(String subject, String body, String... to) throws CentaurException {
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
}
