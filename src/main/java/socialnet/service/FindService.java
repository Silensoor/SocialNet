package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.response.CommonRs;
import socialnet.api.response.PersonRs;
import socialnet.api.response.PostRs;
import socialnet.exception.EmptyEmailException;
import socialnet.repository.mapper.PostsMapper;
import socialnet.mappers.PersonMapper;
import socialnet.model.Person;
import socialnet.model.Post;
import socialnet.model.Post2Tag;
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
    private final TagService tagService;
    private final PersonMapper personMapper;
    private final PostsMapper postsMapper;

    public CommonRs<List<PostRs>> getPostsByQuery(String jwtToken, String author, Long dateFrom,
                                                  Long dateTo, Integer offset, Integer perPage,
                                                  String[] tags, String text) {
        String email = jwtUtils.getUserEmail(jwtToken);
        Person personsEmail = personRepository.findPersonsEmail(email);
        List<Post> postTotal;
        List<PostRs> postRsList = new ArrayList<>();
        List<Post> postList;
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            postList = postRepository.findPostStringSql(author, dateFrom, dateTo, text, perPage, offset, tags);
            if (postList == null) {
                throw new EmptyEmailException("Field 'author' not found");
            } else {
                postTotal = new ArrayList<>(postList);
            }
            if (tags != null) {
                final List<Post> postList1 = comparisonOfSelectionWithTags(postList, tags);
                if (postList1 != null) {
                    postTotal = new ArrayList<>(postList1);
                } else {
                    postTotal = new ArrayList<>();
                }
            }
        }
        int count = 0;
        for (Post post2 : postTotal) {
            if (count >= offset && count < offset + perPage) {
                int postId = post2.getId().intValue();
                PostService.Details details1 = postService.getDetails(post2.getAuthorId(), postId, jwtToken);
                PostRs postRs = postsMapper.toRs(post2, details1);
                postRsList.add(postRs);
            }
            count = count + 1;
        }
        postRsList.sort(Comparator.comparing(PostRs::getTime));
        return new CommonRs<>(postRsList, perPage, offset, perPage, System.currentTimeMillis(), (long) postList.size());
    }

    private List<Post> comparisonOfSelectionWithTags(List<Post> postList, String[] tags) {
        List<Post> postTotal = new ArrayList<>();
        List<Post2Tag> post2TagList = tagService.getPostByQueryTags(tags);
        final List<Post> postStringSql2 = postRepository.findPostStringSql2(post2TagList);
        if (postStringSql2 != null) {
            ArrayList<Post> posts = new ArrayList<>(postStringSql2);
            if (!postList.isEmpty()) {
                postList.forEach((post) -> {
                    posts.forEach((post1) -> {
                        if (post.getId().equals(post1.getId())) {
                            postTotal.add(post);
                        }
                    });
                });
            }
            return postTotal;
        } else {
            return null;
        }
    }

    public CommonRs<List<PersonRs>> findPersons(Object[] args) {
        String email = jwtUtils.getUserEmail((String) args[0]);
        Person personsEmail = personRepository.findPersonsEmail(email);
        List<Person> personList;
        List<PersonRs> personRsList = new ArrayList<>();
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            personList = personRepository.findPersonsQuery(args);
            if (personList == null) {
                personList = new ArrayList<>();
            }
            int count = 0;
            for (Person person : personList) {
                if (count >= (Integer) args[7] && count < (Integer) args[7] + (Integer) args[8]) {
                    PersonRs personRs = personMapper.toDTO(person);
                    personRsList.add(personRs);
                }
                count = count + 1;
            }
        }
        personRsList.sort(Comparator.comparing(PersonRs::getRegDate));
        return new CommonRs<>(personRsList, (Integer) args[8], (Integer) args[7], (Integer) args[8],
                System.currentTimeMillis(), (long) personList.size());
    }
}

