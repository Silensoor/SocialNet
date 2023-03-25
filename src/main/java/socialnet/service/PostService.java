package socialnet.service;

import liquibase.repackaged.org.apache.commons.lang3.tuple.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.dto.CommentRs;
import socialnet.dto.PersonRs;
import socialnet.dto.PostRs;
import socialnet.model.Comment;
import socialnet.model.Person;
import socialnet.model.Post;
import socialnet.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PersonRepository personRepository;
    private final TagRepository tagRepository;
    private final LikeRepository likeRepository;
    private final PersonService authorService;
    private final CommentService commentService;

    public Pair<Integer, List<PostRs>> getAllPosts(Integer offset, Integer perPage) {
        List<Post> posts = postRepository.findAll();
        List<PostRs> result = new ArrayList<>();
        for (Post post : posts) {
            PostRs postRs = convertToPostRs(post);
            result.add(postRs);
        }

        return Pair.of(result.size(), result.stream().skip(offset).limit(perPage).collect(Collectors.toList()));
    }

    public PostRs convertToPostRs(Post post) {
        PostRs postRs = new PostRs();
        Person person = personRepository.findById(post.getAuthorId());
        PersonRs personRs =  authorService.convertToPersonRs(person);
        postRs.setAuthor(personRs);
        List<Comment> comments = commentRepository.findByPostId(post.getId());
        List<CommentRs> commentsRs = comments.stream().map(commentService::convertToCommentRs).collect(Collectors.toList());
        postRs.setComments(commentsRs);
        postRs.setId(post.getId());
        postRs.setIsBlocked(post.getIsBlocked());
        int likesCount = likeRepository.findCountByPersonId(person.getId());
        postRs.setLikes(likesCount);
        postRs.setMyLike(null);
        postRs.setPostText(post.getPostText());
        List<String> tags = tagRepository.findByPostId(post.getId());
        postRs.setTags(tags);
        postRs.setTime(post.getTime().toString());
        postRs.setTitle(post.getTitle());
        postRs.setType(null);

        return postRs;
    }
}
