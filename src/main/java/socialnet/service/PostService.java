package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.response.*;
import socialnet.mappers.CommentMapper;
import socialnet.mappers.PersonMapper;
import socialnet.mappers.PostMapper;
import socialnet.model.Comment;
import socialnet.model.Person;
import socialnet.model.Post;
import socialnet.model.Tag;
import socialnet.model.enums.FriendshipStatusTypes;
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

    public CommonRs<List<PostRs>> getAllPosts(Integer offset, Integer perPage) {
        List<Post> posts = postRepository.findAll();
        List<PostRs> list = new ArrayList<>();
        for (Post post : posts) {
            PostRs postRs = convertToPostRs(post);
            list.add(postRs);
        }
        CommonRs<List<PostRs>> result = new CommonRs<>();
        result.setOffset(offset);
        result.setPerPage(perPage);
        result.setTimestamp(System.currentTimeMillis());
        result.setItemPerPage(perPage);
        result.setTotal((long) list.size());
        result.setData(list.stream().skip(offset).limit(perPage).collect(Collectors.toList()));

        return result;
    }

    public PostRs convertToPostRs(Post post) {
        Person person = personRepository.findById(post.getAuthorId());
        PersonRs personRs = PersonMapper.INSTANCE.toDTO(person);
        personRs.setOnline(null);
        personRs.setWeather(new WeatherRs());
        personRs.setIsBlockedByCurrentUser(null);
        personRs.setFriendStatus(FriendshipStatusTypes.FRIEND.name());
        personRs.setCurrency(new CurrencyRs());
        List<Comment> comments = commentRepository.findByPostId(post.getId());
        List<CommentRs> commentsRs = convertToCommentRs(comments);
        int likesCount = likeRepository.findCountByPersonId(person.getId());
        List<Tag> tags = tagRepository.findByPostId(post.getId());

        PostRs postRs = PostMapper.INSTANCE.toDTO(post);
        postRs.setAuthor(personRs);
        postRs.setComments(commentsRs);
        postRs.setLikes(likesCount);
        postRs.setMyLike(null);
        postRs.setTags(tags.stream().map(Tag::getTag).collect(Collectors.toList()));
        postRs.setType(null);

        return postRs;
    }

    private List<CommentRs> convertToCommentRs(List<Comment> comments) {
        List<CommentRs> result = new ArrayList<>(comments.size());
        for (Comment comment : comments) {
            CommentRs commentRs = CommentMapper.INSTANCE.toDTO(comment);
            Person person = personRepository.findById(comment.getAuthorId());
            commentRs.setAuthor(PersonMapper.INSTANCE.toDTO(person));
            commentRs.setLikes(null);
            commentRs.setMyLike(null);
            commentRs.setSubComments(null);
            result.add(commentRs);
        }

        return result;
    }
}
