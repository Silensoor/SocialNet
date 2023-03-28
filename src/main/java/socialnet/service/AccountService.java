package socialnet.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.account.RegisterRq;
import socialnet.api.account.RegisterRs;
import socialnet.exception.RegisterException;
import socialnet.model.Captcha;
import socialnet.model.Person;
import socialnet.repository.CaptchaRepository;
import socialnet.repository.PersonRepository;

import java.sql.Timestamp;

@Service
@AllArgsConstructor
public class AccountService {
    private final PersonRepository personRepository;
    private final CaptchaRepository captchaRepository;

    public RegisterRs getRegisterData(RegisterRq regRequest) {
        validateFields(regRequest);

        Person person = new Person();
        person.setEmail(regRequest.getEmail());
        person.setFirstName(regRequest.getFirstName());
        person.setLastName(regRequest.getLastName());
        person.setPassword(regRequest.getPasswd1());
        person.setRegDate(new Timestamp(System.currentTimeMillis()));

        personRepository.savePerson(person);

        RegisterRs registerRs = new RegisterRs();
        registerRs.setMessage("OK");
        registerRs.setEmail(regRequest.getEmail());
        registerRs.setTimestamp(new Timestamp(System.currentTimeMillis()));

        return registerRs;
    }

    private void validateFields(RegisterRq regRequest) {
        if (!regRequest.getPasswd1().equals(regRequest.getPasswd2())) {
            throw new RegisterException("Password not equals");
        }

        if (personRepository.findPersonByEmail(regRequest.getEmail()) != null) {
            throw new RegisterException("Email already exists");
        }

        Captcha captcha = captchaRepository.findCaptchaBySecretCode(regRequest.getCodeSecret());

        if (captcha == null) {
            throw new RegisterException("Wrong code");
        }

        if (!regRequest.getCode().equals(captcha.getCode())) {
            throw new RegisterException("Wrong code");
        }
    }
}
