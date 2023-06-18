package socialnet.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.response.CaptchaRs;
import socialnet.model.Captcha;
import socialnet.repository.CaptchaRepository;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CaptchaService {
    private final CaptchaRepository captchaRepository;

    public CaptchaRs getCaptchaData() {
        Cage cage = new GCage();
        String code = randomCode(5);
        String image = "data:image/png;base64," + Base64.getEncoder().encodeToString(cage.draw(code));

        Captcha captcha = new Captcha();
        captcha.setCode(code);
        captcha.setSecretCode(code);
        captcha.setTime(new Timestamp(System.currentTimeMillis()));

        captchaRepository.save(captcha);

        return new CaptchaRs(code, image);
    }

    private String randomCode(int length) {
        final String nums = "0123456789";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(nums.charAt(rnd.nextInt(nums.length())));
        }

        return sb.toString();
    }
}
