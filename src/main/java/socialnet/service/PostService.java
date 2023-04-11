package socialnet.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.response.*;
import socialnet.dto.PostRq;
import socialnet.exception.EmptyEmailException;
import socialnet.mapper.PostCommentMapper;
import socialnet.mapper.PostsMapper;
import socialnet.mappers.CommentMapper;
import socialnet.mappers.PersonMapper;
import socialnet.mappers.PostMapper;
import socialnet.model.*;

import socialnet.model.Comment;
import socialnet.model.Person;
import socialnet.model.Post;
import socialnet.model.Tag;
import socialnet.model.enums.FriendshipStatusTypes;
import socialnet.repository.*;
import socialnet.security.jwt.JwtUtils;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PersonRepository personRepository;
    private final TagRepository tagRepository;
    private final LikeRepository likeRepository;
    private final JwtUtils jwtUtils;
    private final PostCommentRepository postCommentRepository;
    private final PostMapper postMapper;
    public final PostsMapper postsMapper;
    private final PostCommentMapper postCommentMapper;
    private final TagService tagService;

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

    public CommonRs<List<PostRs>> getFeeds(String jwtToken, int offset, int perPage) {
        List<Post> posts = postRepository.findAll();
        List<PostRs> postRsList = new ArrayList<>();
        for (Post post : posts) {
            int postId = post.getId().intValue();
            Details details = getDetails(post.getAuthorId(), postId, jwtToken);
            PostRs postRs = postsMapper.toRs(post, details);
            postRsList.add(postRs);
        }
        postRsList.sort(Comparator.comparing(PostRs::getTime));
        return new CommonRs<>(postRsList, perPage, offset, perPage, System.currentTimeMillis(), (long) postRsList.size());
    }

    public Details getDetails(long authorId, int postId, String jwtToken) {
        Person author = getAuthor(authorId);
        List<Like> likes = getLikes(postId);
        List<Tag> tags = getTags(postId);
        List<String> tagsStrings = tags.stream().map(Tag::getTag).collect(Collectors.toList());
        Person authUser = getAuthUser(jwtToken);
        List<PostComment> postComments = getPostComments(postId);
        List<CommentRs> comments = getComments(postComments, jwtToken);
        return new Details(author, likes, tagsStrings, authUser.getId(), comments);
    }

    List<CommentRs> getComments(List<PostComment> postComments, String jwtToken) {
        List<CommentRs> comments = new ArrayList<>();
        for (PostComment postComment : postComments) {
            int commentId = postComment.getId().intValue();
            Person author = getAuthor(postComment.getAuthorId());
            List<PostComment> subCommentsList = getPostComments(commentId);
            List<CommentRs> subComments = getComments(subCommentsList, jwtToken);
            List<Like> likes = getLikes(commentId);
            Person authUser = personRepository.findByEmail(jwtUtils.getUserEmail(jwtToken));
            long authUserId = authUser.getId();
            CommentRs commentRs = postCommentMapper.toDTO(author, postComment, subComments, likes, authUserId);
            comments.add(commentRs);
        }
        return comments;
    }

    public CommonRs<PostRs> createPost(PostRq postRq, int id, Integer publishDate, String jwtToken) {
        personRepository.findById((long) id);
        Post post = postsMapper.toModel(postRq, publishDate, id);
        int postId = postRepository.save(post);
        tagService.createTags(postRq.getTags(), postId);
        Person author = personRepository.findById((long) id);
        Details details = getDetails(author.getId(), postId, jwtToken);
        PostRs postRs = postsMapper.toRs(post,details);
        return new CommonRs<>(postRs, System.currentTimeMillis());
    }

    public CommonRs<PostRs> getPostById(int postId, String jwtToken) {
        Post post = postRepository.findById(postId);
        Person author = getAuthor(post.getAuthorId());
        Details details = getDetails(author.getId(), postId, jwtToken);
        PostRs postRs = postsMapper.toRs(post, details);
        return new CommonRs<>(postRs, System.currentTimeMillis());
    }

    Person getAuthor(long id) {
        return personRepository.findById(id);
    }
    private List<Like> getLikes(int id) {
        return likeRepository.getLikesByEntityId(id);
    }
    private List<Tag> getTags(int id) {
        return tagRepository.getTagsByPostId(id);
    }
    Person getAuthUser(String jwtToken) {

        Person person = new Person();
        person.setId(1L);
        return person;
    }
    List<PostComment> getPostComments(int id) {
        return postCommentRepository.getCommentsByPostId(id);
    }

    public CommonRs<PostRs> updatePost(int id, PostRq postRq, String jwtToken) {
        Post postFromDB = postRepository.findById(id);
        int publishDate = (int) postFromDB.getTime().getTime();
        Post post = postsMapper.toModel(postRq, publishDate, id);
        postRepository.updateById(id, post);
        Post newPost = postRepository.findById(id);
        Person author = getAuthor(newPost.getAuthorId());
        List<Like> likes = getLikes(newPost.getId().intValue());
        List<Tag> tags = getTags(newPost.getId().intValue());
        List<PostComment> postComments = getPostComments(newPost.getId().intValue());
        List<CommentRs> comments = getComments(postComments, jwtToken);
        Details details = getDetails(author.getId(), newPost.getId().intValue(), jwtToken);
        PostRs postRs = postsMapper.toRs(newPost, details);
        return new CommonRs<>(postRs, System.currentTimeMillis());
    }

    public CommonRs<PostRs> markAsDelete(int id, String jwtToken) {
        Post postFromDB = postRepository.findById(id);
        postFromDB.setIsDeleted(true);
        postRepository.updateById(id, postFromDB);
        Person author = getAuthor(postFromDB.getId());
        Details details = getDetails(author.getId(), postFromDB.getId().intValue(), jwtToken);
        PostRs postRs = postsMapper.toRs(postFromDB, details);
        return new CommonRs<>(postRs, System.currentTimeMillis());
    }

    public CommonRs<PostRs> recoverPost(int id, String jwtToken) {
        Post postFromDB = postRepository.findById(id);
        postFromDB.setIsDeleted(false);
        Person author = getAuthor(postFromDB.getAuthorId());
        Details details = getDetails(author.getId(), postFromDB.getId().intValue(), jwtToken);
        PostRs postRs = postsMapper.toRs(postFromDB, details);
        return new CommonRs<>(postRs, System.currentTimeMillis());
    }

    @Data
    @AllArgsConstructor
    public class Details {
        Person author;
        List<Like> likes;
        List<String> tags;
        Long authUserId;
        List<CommentRs> comments;
    }
}
