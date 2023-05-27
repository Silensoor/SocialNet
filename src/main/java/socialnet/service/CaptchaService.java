package socialnet.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import socialnet.api.response.CaptchaRs;
import socialnet.model.Captcha;
import socialnet.repository.CaptchaRepository;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaptchaService {
    private final CaptchaRepository captchaRepository;

    public CaptchaRs getCaptchaData() {
        Cage cage = new GCage();
        String chrs = "0123456789";
        SecureRandom secureRandom;
        String code = "48526";

        try {
            secureRandom = SecureRandom.getInstanceStrong();
            code = secureRandom.ints(5, 0, chrs.length()).mapToObj(chrs::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
        }

        String image = "data:image/png;base64," + Base64.getEncoder().encodeToString(cage.draw(code));

        Captcha captcha = new Captcha();
        captcha.setCode(code);
        captcha.setSecretCode(code);
        captcha.setTime(new Timestamp(System.currentTimeMillis()));

        captchaRepository.save(captcha);

        return new CaptchaRs(code, image);
    }
}
