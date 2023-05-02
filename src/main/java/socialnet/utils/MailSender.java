package socialnet.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class MailSender {
    @Value("${mail.smtpHost}")
    private String mailSmtpHost;

    @Value("${mail.smtpPort}")
    private String mailSmtpPort;

    @Value("${mail.authUser}")
    private String mailAuthUser;

    @Value("${mail.authPass}")
    private String mailAuthPass;

    public void send(String to, String subject, String text) throws Exception {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", mailSmtpHost);
        prop.put("mail.smtp.port", mailSmtpPort);
        prop.put("mail.smtp.ssl.trust", "*");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailAuthUser, mailAuthPass);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mailAuthUser, "Zerone SocialNet"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(text, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}
