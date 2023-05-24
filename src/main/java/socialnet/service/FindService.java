package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.response.CommonRs;
import socialnet.api.response.PersonRs;
import socialnet.api.response.PostRs;
import socialnet.exception.EmptyEmailException;
import socialnet.mappers.PersonMapper;
import socialnet.model.Person;
import socialnet.model.Post;
import socialnet.repository.PersonRepository;
import socialnet.repository.PostRepository;
import socialnet.security.jwt.JwtUtils;
import socialnet.utils.PostServiceDetails;

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

    public CommonRs<List<PostRs>> getPostsByQuery(String jwtToken, String author, Long dateFrom,
                                                  Long dateTo, Integer offset, Integer perPage,
                                                  String[] tags, String text)
    {
        String email = jwtUtils.getUserEmail(jwtToken);
        Person person = personRepository.findByEmail(email);

        if (person == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        }

        List<PostRs> postRsList = new ArrayList<>();
        long postListAll;
        List<Post> postList = postRepository.findPostStringSql(findAuthor(author), dateFrom, dateTo, text,
                    perPage, offset, tags, false);
        postListAll = Integer.toUnsignedLong(postRepository.findPostStringSqlAll(findAuthor(author), dateFrom,
                    dateTo, text, tags, true));

        postList.forEach(post -> {
            int postId = post.getId().intValue();
            PostServiceDetails details1 = postService.getDetails(post.getAuthorId(), postId, jwtToken);
            PostRs postRs = PostService.setPostRs(post, details1);
            postRsList.add(postRs);
        });

        postRsList.sort(Comparator.comparing(PostRs::getTime).reversed());

        return new CommonRs<>(postRsList, postRsList.size(), offset, perPage, System.currentTimeMillis(),
                    postListAll);
    }

    public CommonRs<List<PersonRs>> findPersons(String authorization, Integer ageFrom, Integer ageTo, String city,
                                                String country, String firstName, String lastName,
                                                Integer offset, Integer perPage) {
        String email = jwtUtils.getUserEmail(authorization);
        Person personsEmail = personRepository.findByEmail(email);
        long findPersonQueryAll;
        List<Person> personList;
        List<PersonRs> personRsList = new ArrayList<>();
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            personList = personRepository.findPersonsQuery(ageFrom, ageTo, city, country,
                    firstName, lastName, offset, perPage, false, Math.toIntExact(personsEmail.getId()));
            findPersonQueryAll = Integer.toUnsignedLong(personRepository.findPersonsQueryAll(ageFrom,
                    ageTo, city, country, firstName, lastName, true, Math.toIntExact(personsEmail.getId())));

            personList.forEach(person -> {
                PersonRs personRs = PersonMapper.INSTANCE.toDTO(person);
                personRsList.add(personRs);
            });
        }
        personRsList.sort(Comparator.comparing(PersonRs::getRegDate).reversed());
        return new CommonRs<>(personRsList, personRsList.size(), offset, perPage, System.currentTimeMillis(),
                findPersonQueryAll);
    }

    public Integer findAuthor(String author) {
        if (author.isBlank()) {
            return 0;
        }

        Person person = personRepository.findPersonByFirstOrLastName(author);

        if (person == null) {
            return 0;
        }

        return person.getId().intValue();

        /*if (author.trim().indexOf(" ") > 0) {
            if (personRepository.findPersonByFirstOrLastName(author) != null) {
                return Math.toIntExact(personRepository.findPersonByFirstOrLastName(author).getId());
            } else {
                return 0;
            }
        } else {
            return null;
        }*/
    }
}
