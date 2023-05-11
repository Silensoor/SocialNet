package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ResourceBundle;

@Service
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender javaMailSender;
    @Value("${mail.from}")
    private String mailFrom;

    public void send(String emailTo, String subject, String message) {
        MimeMessage htmlMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(htmlMessage);
        try {
            helper.setFrom(mailFrom);
            helper.setTo(emailTo);
            helper.setSubject(subject);
            helper.setText(message,true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        javaMailSender.send(htmlMessage);
    }
}
