package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.response.CommonRs;
import socialnet.api.response.PersonRs;
import socialnet.api.response.PostRs;
import socialnet.exception.EmptyEmailException;
import socialnet.mapper.PostsMapper;
import socialnet.mappers.PersonMapper;
import socialnet.model.Person;
import socialnet.model.Post;
import socialnet.repository.PersonRepository;
import socialnet.repository.PostRepository;
import socialnet.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FindService {
    private final JwtUtils jwtUtils;
    private final PersonRepository personRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final PersonMapper personMapper;
    private final PostsMapper postsMapper;

    public CommonRs<List<PostRs>> getPostsByQuery(String jwtToken, String author, Long dateFrom,
                                                  Long dateTo, Integer offset, Integer perPage,
                                                  String[] tags, String text) {
        String email = jwtUtils.getUserEmail(jwtToken);
        Person personsEmail = personRepository.findPersonsEmail(email);
        List<PostRs> postRsList = new ArrayList<>();
        List<Post> postList;
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            postList = postRepository.findPostStringSql(author, dateFrom, dateTo, text, perPage, offset, tags);
            postList.stream().forEach(post -> {
                int postId = post.getId().intValue();
                PostService.Details details1 = postService.getDetails(post.getAuthorId(), postId, jwtToken);
                PostRs postRs = postsMapper.toRs(post, details1);
                postRsList.add(postRs);
            });
            postRsList.sort(Comparator.comparing(PostRs::getTime));
            return new CommonRs<>(postRsList, perPage, offset, perPage, System.currentTimeMillis(), (long) postList.size());
        }
    }

    public CommonRs<List<PersonRs>> findPersons(String authorization, Integer age_from, Integer age_to, String city,
                                                String country, String first_name, String last_name, Integer offset, Integer perPage) {
        String email = jwtUtils.getUserEmail(authorization);
        Person personsEmail = personRepository.findPersonsEmail(email);
        List<Person> personList;
        List<PersonRs> personRsList = new ArrayList<>();
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            personList = personRepository.findPersonsQuery(age_from, age_to, city, country,
                    first_name, last_name, offset, perPage);
            if (personList == null) {
                personList = new ArrayList<>();
            }
            personList.forEach((person) -> {
                PersonRs personRs = personMapper.toDTO(person);
                personRsList.add(personRs);
            });
        }
        personRsList.sort(Comparator.comparing(PersonRs::getRegDate));
        return new CommonRs<>(personRsList, perPage, offset, perPage, System.currentTimeMillis(), (long) personList.size());
    }
}

