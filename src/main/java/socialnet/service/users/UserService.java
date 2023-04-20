package socialnet.service.users;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PersonRepository personRepository;
    public Person getAuthPerson() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return personRepository.getPersonByEmail(email);
    }
}
