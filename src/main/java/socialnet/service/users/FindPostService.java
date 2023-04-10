package socialnet.service.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import socialnet.api.response.CommonRs;
import socialnet.api.response.ErrorRs;
import socialnet.api.response.PostRs;
import socialnet.exception.EmptyEmailException;
import socialnet.model.Person;

import socialnet.model.Post;
import socialnet.model.Post2Tag;
import socialnet.model.Tag;
import socialnet.repository.*;
import socialnet.security.jwt.JwtUtils;
import socialnet.service.PostService;
import socialnet.service.TagService;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FindPostService {
    private final PersonRepository personRepository;
    private final PostRepository postsRepository;
    private final Post2TagRepository post2TagRepository;
    private final CommentRepository commentsRepository;
    private final LikeRepository likesRepository;
    private final JwtUtils jwtUtils;
    private final TagService tagService;
    private final PostService postService;

    public ResponseEntity<?> findPostsByUserId(String authorization,
                                               Long userId,
                                               Integer itemPerPage,
                                               Integer offset) {


        if (!jwtUtils.validateJwtToken(authorization)) {//401
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String userName = jwtUtils.getUserEmail(authorization);
        if (userName.isEmpty()) {
            return new ResponseEntity<>(
                    new ErrorRs("EmptyEmailException", "Field 'email' is empty"), HttpStatus.BAD_REQUEST);  //400
        }

        Person person = personRepository.findByEmail(userName);
        if (person == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);  //403
        }

        List<Post> posts = postsRepository.findPostsByUserId(userId);

        for (Post post : posts) {
            List<String> listTags = post2TagRepository.findTagsByPostId(post.getId())
                    .stream()
                    .map(Tag::getTag)
                    .collect(Collectors.toList());

            Integer likesCount = likesRepository.findCountByPersonId(post.getId());
            Boolean isMyLike = likesRepository.isMyLike("'Post'", post.getId(), person.getId());

            //PersonRs personRs = PersonMapper.INSTANCE.personToPersonRs(person);

            //PostRs data = new PostRs(personRs, listTags, likesCount, isMyLike);
        }

        Integer perPage = 20;
        Long timeStamp = System.currentTimeMillis();
        Long total = 0L;

        //List<PostRs> posts = new ArrayList<>();

        return ResponseEntity.ok(posts);
    }



//    private List<CommentRs> findCommentsByPostId(PersonRs personRs, Long postId) {
//        List<Comment> comments = commentsRepository.findCommentsByPostId(postId);
//        CommentRs commentRs = new CommentRs(personRs);
//
//    }

    public CommonRs<List<PostRs>> getPostsByQuery(String jwtToken, String author, String dateFrom,
                                                  String dateTo, Integer offset, Integer perPage,
                                                  String[] tags, String text) throws ParseException {
        String email = jwtUtils.getUserEmail(jwtToken);
        //String email = "nwickey2@ibm.com";
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
                    dateTo, offset, perPage, tags, text);
            if (!sql.equals("SELECT * FROM posts WHERE")) {
                postList = postsRepository.findPostStringSql(sql);
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
            PostService.Details details = postService.getDetails(post2.getAuthorId(), postId, jwtToken);
            PostRs postRs = postService.postsMapper.toRs(post2, details);
            postRsList.add(postRs);
        }
        postRsList.sort(Comparator.comparing(PostRs::getTime));
        return new CommonRs<>(postRsList, perPage, offset, perPage,
                System.currentTimeMillis(), (long) postRsList.size());
    }

    private List<Post> comparisonOfSelectionWithTags(List<Post> postList, String[] tags){
        List<Post> postTotal = new ArrayList<>();
        List<Post> posts = new ArrayList<>();
        List<Post2Tag> post2TagList = tagService.getPostByQueryTags(tags);
        String sql2 = createSqlPost2Tag(post2TagList);
        posts.addAll(postsRepository.findPostStringSql(sql2));
        if (!postList.isEmpty()) {
            for (Post post : postList){
                for (Post post1 : posts){
                    if (post.getId().equals(post1.getId())){
                        postTotal.add(post);
                    }
                }
            }
        }
        return postTotal;
    }

    private String createSqlPost(String author, String dateFrom,
                                 String dateTo, Integer offset, Integer perPage,
                                 String[] tags, String text) throws ParseException {
        String sql = "SELECT * FROM posts WHERE";
        if (author != null && !author.equals("")) {
            sql = sql + " author_id = '" + author + "' AND ";
        }
        if (dateFrom != null) {
            Timestamp dateFrom1 = parseDate(dateFrom);
            sql = sql + " time > '" + dateFrom1 + "' AND ";
        }
        if (dateTo != null) {
            Timestamp dateTo1 = parseDate(dateTo);
            sql = sql + " time < '" + dateTo1  + "' AND ";
        }
        if (text != null && text != "") {
            sql = sql + " post_text LIKE '%" + text + "%' AND ";
        }
        String str = sql.substring(sql.length() - 5);
        if (str.equals(" AND ")){
            sql = sql.substring(0, sql.length() - 5);
        }
        return sql;
    }

    private String createSqlPost2Tag(List<Post2Tag> post2TagList){
        StringBuilder sql = new StringBuilder("SELECT * FROM post WHERE");
        for(Post2Tag post2Tag : post2TagList){
            if (post2Tag.getPostId() != 0) {
                sql.append(" Id = '").append(post2Tag).append("' OR ");
            }
        }
        if (sql.substring(sql.length() - 4).equals("' OR ")){
            sql.substring(0, sql.length() - 4);
        }
        return sql.toString();
    }

    private Timestamp parseDate(String str) throws ParseException {
        //SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Long parseInt = Long.parseLong(str);
        Date date = new Date(parseInt);
        return new Timestamp(date.getTime());
    }

}
