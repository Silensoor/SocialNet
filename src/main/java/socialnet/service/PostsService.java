package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import socialnet.dto.PersonRs;
import socialnet.mapper.PersonMapper;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;
import socialnet.repository.PostsRepository;

@Service
@RequiredArgsConstructor
public class PostsService {
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final PostsRepository postsRepository;
    public ResponseEntity<String> getFeeds(int offset, int perPage) {
        postsRepository.getFeeds();
//        PersonRs personRs = personMapper.toDTO(personRepository.getPersonById(0));
//        System.out.println(personRs.getCurrency().getEuro() + " " + personRs.getCurrency().getUsd());
//        System.out.println(personRs.isBlockedByCurrentUser());
        return null;
    }
}
