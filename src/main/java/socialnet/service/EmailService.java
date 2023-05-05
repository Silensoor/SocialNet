package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import socialnet.security.jwt.JwtUtils;

import java.util.ResourceBundle;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailSender emailSender;

    private static final ResourceBundle textProperties = ResourceBundle.getBundle("text");
    private final JwtUtils jwtUtils;

    public void passwordChangeConfirm(String authorization) {
        String email = jwtUtils.getUserEmail(authorization);
        String token = jwtUtils.generateJwtToken(email);

        String message =
                "<p><a href=\"" +
                        textProperties.getString("base.url")
                                .concat("change-password?token=")
                                .concat(token)
                                .concat("\">Confirm change [PASSWORD]!</a></p>\n");

        emailSender.send(email,
                "Подтверждение изменения пароля.",
                message);

    }

    public void shiftEmailConfirm(String authorization) {
        String email = jwtUtils.getUserEmail(authorization);
        String token = jwtUtils.generateJwtToken(email);

        String message =
                "<p><a href=\"" +
                textProperties.getString("base.url")
                        .concat("shift-email?token=")
                        .concat(token)
                        .concat("\">Confirm change [EMAIL]!</a></p>\n");

        emailSender.send(email,
                "Подтверждение изменения email.",
                message);
    }
}
