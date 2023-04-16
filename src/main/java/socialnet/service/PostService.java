package socialnet.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import socialnet.api.request.PostRq;
import socialnet.api.response.*;
import socialnet.mapper.PostCommentMapper;
import socialnet.mapper.PostsMapper;
import socialnet.mappers.CommentMapper;
import socialnet.mappers.PersonMapper;
import socialnet.mappers.PostMapper;
import socialnet.model.*;
import socialnet.model.enums.FriendshipStatusTypes;
import socialnet.repository.*;
import socialnet.security.jwt.JwtUtils;
import java.sql.Timestamp;
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
    private final PostsMapper postsMapper;
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
        List<Post> postList = postRepository.findAll(offset, perPage);
        postList.sort(Comparator.comparing(Post::getTime).reversed());
        List<PostRs> postRsList = new ArrayList<>();
        for (Post post : postList) {
            int postId = post.getId().intValue();
            Details details = getDetails(post.getAuthorId(), postId, jwtToken);
            PostRs postRs = postsMapper.toRs(post, details);
            postRsList.add(postRs);
        }
        int itemPerPage = offset / perPage;
        return new CommonRs<>(postRsList, itemPerPage, offset, perPage, System.currentTimeMillis(), (long) postRsList.size());
    }

    Details getDetails(long authorId, int postId, String jwtToken) {
        Person author = getAuthor(authorId);
        List<Like> likes = getLikes(postId).stream().filter(l -> l.getType().equals("Post")).collect(Collectors.toList());
        List<Tag> tags = getTags(postId);
        List<String> tagsStrings = tags.stream().map(Tag::getTag).collect(Collectors.toList());
        Person authUser = getAuthUser(jwtToken);
        List<Comment> postComments = getPostComments(postId);
        List<CommentRs> comments = getComments(postComments, jwtToken);
        comments = comments.stream().filter(c -> c.getParentId() == 0).collect(Collectors.toList());
        return new Details(author, likes, tagsStrings, authUser.getId(), comments);
    }

    private List<CommentRs> getComments(List<Comment> postComments, String jwtToken) {
        List<CommentRs> comments = new ArrayList<>();
        for (Comment postComment : postComments) {
            int commentId = postComment.getId().intValue();
            Person author = getAuthor(postComment.getAuthorId());
            List<Comment> subCommentsList = getSubCommentList(postComment.getId());
            assert subCommentsList != null;
            List<CommentRs> subComments = getSubComments(subCommentsList, jwtToken);
            List<Like> likes = getLikes(commentId).stream().filter(l -> l.getType().equals("Comment")).collect(Collectors.toList());
            Person authUser = personRepository.findByEmail(jwtUtils.getUserEmail(jwtToken));
            long authUserId = authUser.getId();
            CommentRs commentRs = postCommentMapper.toDTO(author, postComment, subComments, likes, authUserId);
            comments.add(commentRs);
        }
        return comments;
    }

    private List<CommentRs> getSubComments(List<Comment> parentCommentsList, String jwtToken) {
        List<CommentRs> comments = new ArrayList<>();
        for (Comment parentComment : parentCommentsList) {
            int commentId = parentComment.getId().intValue();
            Person author = getAuthor(parentComment.getAuthorId());
            List<Comment> subCommentsList = getSubCommentList(parentComment.getId());
            assert subCommentsList != null;
            List<Like> likes = getLikes(commentId).stream().filter(l -> l.getType().equals("Comment")).collect(Collectors.toList());
            Person authUser = personRepository.findByEmail(jwtUtils.getUserEmail(jwtToken));
            long authUserId = authUser.getId();
            CommentRs commentRs = postCommentMapper.toDTO(author, parentComment, new ArrayList<>(), likes, authUserId);
            comments.add(commentRs);
        }
        return comments;
    }

    private List<Comment> getSubCommentList(long parentId) {
        return commentRepository.findByPostIdParentId(parentId);
    }

    public CommonRs<PostRs> createPost(PostRq postRq, int id, Integer publishDate, String jwtToken) {
        personRepository.findById((long) id);
        Post post = postsMapper.toModel(postRq, publishDate, id);
        int postId = postRepository.save(post);
        tagRepository.saveAll(postRq.getTags(), postId);
        Person author = personRepository.findById((long) id);
        Details details = getDetails(author.getId(), postId, jwtToken);
        PostRs postRs = postsMapper.toRs(post, details);
        return new CommonRs<>(postRs, System.currentTimeMillis());
    }

    public CommonRs<PostRs> getPostById(int postId, String jwtToken) {
        Post post = postRepository.findById(postId);
        Person author = getAuthor(post.getAuthorId());
        Details details = getDetails(author.getId(), postId, jwtToken);
        PostRs postRs = postsMapper.toRs(post, details);
        postRs.setTags(tagRepository.findByPostId((long) postId).stream().map(Tag::getTag).collect(Collectors.toList()));
        return new CommonRs<>(postRs, System.currentTimeMillis());
    }

    private Person getAuthor(long id) {
        return personRepository.findById(id);
    }

    private List<Like> getLikes(int id) {
        return likeRepository.getLikesByEntityId(id);
    }

    private List<Tag> getTags(int id) {
        return tagRepository.findByPostId((long) id);
    }

    private Person getAuthUser(String jwtToken) {
        return personRepository.findByEmail(jwtUtils.getUserEmail(jwtToken));
    }

    private List<Comment> getPostComments(int id) {
        return commentRepository.findByPostId((long) id);
    }

    public CommonRs<PostRs> updatePost(int id, PostRq postRq, String jwtToken) {
        Post postFromDB = postRepository.findById(id);
        int publishDate = (int) postFromDB.getTime().getTime();
        Post post = postsMapper.toModel(postRq, publishDate, id);
        postRepository.updateById(id, post);
        tagRepository.deleteAll(tagRepository.findByPostId((long) id));
        tagRepository.saveAll(postRq.getTags(), id);
        Post newPost = postRepository.findById(id);
        Person author = getAuthor(newPost.getAuthorId());
        Details details = getDetails(author.getId(), newPost.getId().intValue(), jwtToken);
        PostRs postRs = postsMapper.toRs(newPost, details);
        return new CommonRs<>(postRs, System.currentTimeMillis());
    }

    public CommonRs<PostRs> markAsDelete(int id, String jwtToken) {
        Post postFromDB = postRepository.findById(id);
        postFromDB.setIsDeleted(true);
        postFromDB.setTimeDelete(new Timestamp(System.currentTimeMillis()));
        postRepository.markAsDeleteById(id, postFromDB);
        Person author = getAuthor(postFromDB.getAuthorId());
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

    private Timestamp parseDate(String str) throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date date = parser.parse(str);
        return new Timestamp(date.getTime());
    }

    @Scheduled(cron = "0 0 1 * * *")
    private void hardDeletingPosts() {
        List<Post> deletingPosts = postRepository.findDeletedPosts();
        postRepository.deleteAll(deletingPosts);
        List<Tag> tags = new ArrayList<>();
        List<Like> likes = new ArrayList<>();
        for (Post deletingPost : deletingPosts) {
            tags.addAll(tagRepository.findByPostId(deletingPost.getId()));
            likes.addAll(likeRepository.getLikesByEntityId(deletingPost.getId()));
        }
        tagRepository.deleteAll(tags);
        likeRepository.deleteAll(likes);
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
