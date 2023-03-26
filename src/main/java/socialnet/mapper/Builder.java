package socialnet.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import socialnet.dto.PersonRs;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;

public class Builder {
    @Autowired
    PersonRepository personRepository;
    @Autowired
    PersonMapper personMapper;
    public PersonRs getAuthor(long id) {
        return personMapper.toDTO(personRepository.getPersonById(id));
    }
}
