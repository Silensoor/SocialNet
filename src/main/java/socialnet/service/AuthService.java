package socialnet.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.auth.CaptchaRs;
import socialnet.model.Captcha;
import socialnet.repository.CaptchaRepository;

import java.sql.Timestamp;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CaptchaRepository captchaRepository;

    public CaptchaRs getCaptchaData() {
        Cage cage = new GCage();
        String code = cage.getTokenGenerator().next();
        String image = "data:image/png;base64," + Base64.getEncoder().encodeToString(cage.draw(code));

        Captcha captcha = new Captcha();
        captcha.setCode(code);
        captcha.setSecretCode(code);
        captcha.setTime(new Timestamp(System.currentTimeMillis()));

        captchaRepository.save(captcha);

        return new CaptchaRs(code, image);
    }
}
