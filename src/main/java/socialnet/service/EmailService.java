package socialnet.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailSender emailSender;


    public void send(String emailTo, String subject, String message) {

        emailSender.send(emailTo,
                subject,
                message);

    }
}
