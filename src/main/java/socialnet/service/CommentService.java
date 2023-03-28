package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.dto.CommentRs;
import socialnet.model.Comment;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final PersonRepository personRepository;
    private final PersonService personService;

}
