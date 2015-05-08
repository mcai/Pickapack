/*******************************************************************************
 * Copyright (c) 2010-2012 by Min Cai (min.cai.china@gmail.com).
 *
 * This file is part of the PickaPack library.
 *
 * PickaPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PickaPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PickaPack. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.pickapack.mail;

import com.sun.mail.pop3.POP3SSLStore;
import com.sun.mail.smtp.SMTPTransport;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 *
 * @author Min Cai
 */
public class HotmailHelper {
    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    private static final String POP3_HOST = "pop3.live.com";

    private static Session getSession() {
        Properties pop3Props = new Properties();

        pop3Props.setProperty("mail.pop3.ssl.enable", "true");

        pop3Props.setProperty("mail.pop3s.socketFactory.class", SSL_FACTORY);
        pop3Props.setProperty("mail.pop3s.socketFactory.fallback", "false");
        pop3Props.setProperty("mail.pop3s.port", "995");
        pop3Props.setProperty("mail.pop3s.socketFactory.port", "995");

        pop3Props.setProperty("mail.smtp.host", "smtp.live.com");
        pop3Props.setProperty("mail.smtp.auth", "true");

        pop3Props.setProperty("mail.smtp.starttls.enable","true");

        return Session.getInstance(pop3Props, null);
    }

    /**
     *
     * @param email
     * @param emailPassword
     * @return
     * @throws MessagingException
     */
    public static Store connect(String email, String emailPassword) throws MessagingException {
        Session session = getSession();

        URLName url = new URLName("pop3", POP3_HOST, 995, "", email, emailPassword);
        Store store = new POP3SSLStore(session, url);
        store.connect();
        return store;
    }

    /**
     *
     * @param from
     * @param to
     * @param subject
     * @param body
     * @throws MessagingException
     */
    public static void sendEmail(String from, String to, String subject, String body) throws MessagingException {
        Session session = getSession();
        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(from));

        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

        message.setSubject(subject);
        message.setContent(body, "text/plain");

        SMTPTransport smtpTransport = (SMTPTransport) session.getTransport("smtp");

        smtpTransport.connect("smtp.live.com", from, "aaaxxx");

        smtpTransport.sendMessage(message, message.getAllRecipients());
    }
    
    /**
     *
     * @param args
     * @throws MessagingException
     */
    public static void main(String[] args) throws MessagingException {
        sendEmail("afrhaqmz676@hotmail.com", "min.cai.china@gmail.com", "Hello", "World");
    }
}
