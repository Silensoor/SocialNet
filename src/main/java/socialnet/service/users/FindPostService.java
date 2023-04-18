package socialnet.service.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import socialnet.api.response.ErrorRs;
import socialnet.model.Person;

import socialnet.model.Post;
import socialnet.model.Tag;
import socialnet.repository.*;
import socialnet.security.jwt.JwtUtils;

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


}
