package socialnet.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import socialnet.api.response.CommonRs;
import socialnet.api.response.PersonRs;
import socialnet.api.response.PostRs;
import socialnet.exception.EmptyEmailException;
import socialnet.mapper.PostsMapper;
import socialnet.mappers.PersonMapper;
import socialnet.model.*;
import socialnet.repository.LikeRepository;
import socialnet.repository.PersonRepository;
import socialnet.repository.PostRepository;
import socialnet.repository.TagRepository;
import socialnet.security.jwt.JwtUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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
    private final LikeRepository likeRepository;
    private final TagRepository tagRepository;
    private final PostsMapper postsMapper;

    public CommonRs<List<PostRs>> getPostsByQuery(String jwtToken, String author, String dateFrom,
                                                  String dateTo, Integer offset, Integer perPage,
                                                  String[] tags, String text) throws ParseException {
        String email = jwtUtils.getUserEmail(jwtToken);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        List<Post> postTotal = new ArrayList<>();
        List<PostRs> postRsList = new ArrayList<>();
        if (personsEmail == null) {
            personsEmail = new ArrayList<>();
        }
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Post> postList = new ArrayList<>();
            String sql = createSqlPost(author, dateFrom,
                    dateTo, text);
            if (!sql.equals("SELECT * FROM posts WHERE")) {
                postList = postRepository.findPostStringSql(sql);
                if (postList == null) {
                    postList = new ArrayList<>();
                } else {
                    postTotal.addAll(postList);
                }
            }
            if (tags != null) {
                postTotal.addAll(comparisonOfSelectionWithTags(postList, tags));
            }
        }
        for (Post post2 : postTotal) {
            int postId = post2.getId().intValue();
            PostService.Details details1 = postService.getDetails(post2.getAuthorId(), postId, jwtToken);
            PostRs postRs = postsMapper.toRs(post2, details1);
            postRsList.add(postRs);
        }
        postRsList.sort(Comparator.comparing(PostRs::getTime));
        return new CommonRs<>(postRsList, perPage, offset, perPage,
                System.currentTimeMillis(), (long) postRsList.size());
    }

    private List<Post> comparisonOfSelectionWithTags(List<Post> postList, String[] tags) {
        List<Post> postTotal = new ArrayList<>();
        List<Post2Tag> post2TagList = tagService.getPostByQueryTags(tags);
        String sql2 = createSqlPost2Tag(post2TagList);
        ArrayList<Post> posts = new ArrayList<>(postRepository.findPostStringSql(sql2));
        if (!postList.isEmpty()) {
            for (Post post : postList) {
                for (Post post1 : posts) {
                    if (post.getId().equals(post1.getId())) {
                        postTotal.add(post);
                    }
                }
            }
        }
        return postTotal;
    }

    private String createSqlPost(String author, String dateFrom, String dateTo, String text) throws ParseException {
        String sql = "SELECT * FROM posts WHERE";
        if (author.indexOf(" ") > 0) {
            String firstName = author.substring(0, author.indexOf(" ")).trim();
            String lastName = author.substring(author.indexOf(" ")).trim();
            final Person personsName = personRepository.findPersonsName(firstName, lastName);
            Long idPerson = personsName.getId();
            sql = sql + " author_id = " + idPerson + " AND ";
        }
        if (!dateFrom.equals("")) {
            Timestamp dateFrom1 = parseDate(dateFrom);
            sql = sql + " time > '" + dateFrom1 + "' AND ";
        }
        if (!dateTo.equals("")) {
            Timestamp dateTo1 = parseDate(dateTo);
            sql = sql + " time < '" + dateTo1 + "' AND ";
        }
        if (!text.equals("")) {
            sql = sql + " post_text LIKE '%" + text + "%' AND ";
        }
        String str = sql.substring(sql.length() - 5);
        if (str.equals(" AND ")) {
            sql = sql.substring(0, sql.length() - 5);
        }
        return sql;
    }

    private String createSqlPost2Tag(List<Post2Tag> post2TagList) {
        StringBuilder sql = new StringBuilder("SELECT * FROM post WHERE");
        for (Post2Tag post2Tag : post2TagList) {
            if (post2Tag.getPostId() != 0) {
                sql.append(" Id = '").append(post2Tag).append("' OR ");
            }
        }
        if (sql.substring(sql.length() - 4).equals("' OR ")) {
            sql.delete(sql.length() - 4, sql.length());
        }
        return sql.toString();
    }

    private Timestamp parseDate(String str) {
        long parseInt = Long.parseLong(str);
        Date date = new Date(parseInt);
        return new Timestamp(date.getTime());
    }

    public CommonRs<List<PersonRs>> findPersons(Object[] args) {
        String email = jwtUtils.getUserEmail((String) args[0]);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        List<Person> personList = new ArrayList<>();
        List<PersonRs> personRsList = new ArrayList<>();
        if (personsEmail == null) {
            personsEmail = new ArrayList<>();
        }
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            String sql = createSqlPerson(args);
            if (!sql.equals("SELECT * FROM persons WHERE is_deleted=false AND is_blocked=false AND ")) {
                personList = personRepository.findPersonsQuery(sql);
                if (personList == null) {
                    personList = new ArrayList<>();
                }
            }
            for (Person person : personList) {
                PersonRs personRs = personMapper.toDTO(person);
                personRsList.add(personRs);
            }
        }
        personRsList.sort(Comparator.comparing(PersonRs::getRegDate));
        return new CommonRs<>(personRsList, (Integer) args[8], (Integer) args[7], (Integer) args[8],
                System.currentTimeMillis(), (long) personRsList.size());
    }


    private String createSqlPerson(Object[] args) {
        String sql = "SELECT * FROM persons WHERE is_deleted=false AND is_blocked=false AND ";
        if ((Integer) args[1] > 0) {
            val ageFrom = searchDate((Integer) args[1]);
            sql = sql + " birth_date < '" + ageFrom + "' AND ";
        }
        if ((Integer) args[2] > 0) {
            val ageTo = searchDate((Integer) args[2]);
            sql = sql + " birth_date > '" + ageTo + "' AND ";
        }
        if (!args[3].equals("")) {
            sql = sql + " city = '" + args[3] + "' AND ";
        }
        if (!args[4].equals("")) {
            sql = sql + " country = '" + args[4] + "' AND ";
        }
        if (!args[5].equals("")) {
            sql = sql + " first_name = '" + args[5] + "' AND ";
        }
        if (!args[6].equals("")) {
            sql = sql + " last_name = '" + args[6] + "' AND ";
        }
        String str = sql.substring(sql.length() - 5);
        if (str.equals(" AND ")) {
            return sql.substring(0, sql.length() - 5);
        }
        return sql;
    }

    private Timestamp searchDate(Integer age) {
        val timestamp = new Timestamp(new Date().getTime());
        timestamp.setYear(timestamp.getYear() - age);
        return timestamp;
    }
}

