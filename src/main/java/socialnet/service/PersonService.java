package socialnet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {

    private final PersonRepository personRepository;
    private final LoginService loginService;

    public Object getUserById(String authorization, Integer id) {
        Person person = findUser(id);
        log.info("вход на страницу пользователя " + person.getFirstName());
        return loginService.setLoginRs(authorization,person);
    }

    private Person findUser (Integer id) {
        return personRepository.findById(Long.valueOf(id));
    }
}
