package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import socialnet.dto.PersonRs;
import socialnet.dto.PostRs;
import socialnet.mapper.PersonMapper;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;
import socialnet.repository.PostsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostsService {
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final PostsRepository postsRepository;
    public ResponseEntity<String> getFeeds(int offset, int perPage) {
        List<PostRs> postRsList = postsRepository.getFeeds();
        for (PostRs postRs : postRsList) {
            PersonRs personRs = postRs.getAuthor();
            System.out.println(personRs.getEmail());
            System.out.println(personRs.getFirstName());
        }
        return null;
    }
}
