package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.request.PostRq;
import socialnet.api.response.*;
import socialnet.exception.EntityNotFoundException;
import socialnet.mappers.CommentMapper;
import socialnet.mappers.PersonMapper;
import socialnet.mappers.PostMapper;
import socialnet.model.*;
import socialnet.model.enums.FriendshipStatusTypes;
import socialnet.repository.*;
import socialnet.security.jwt.JwtUtils;
import socialnet.utils.PostServiceDetails;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
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
    private final JwtUtils jwtUtils;

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
            PostServiceDetails details = getDetails(post.getAuthorId(), postId, jwtToken);
            PostRs postRs = setPostRs(post, details);
            postRsList.add(postRs);
        }
        int itemPerPage = offset / perPage;
        return new CommonRs<>(postRsList, itemPerPage, offset, perPage, System.currentTimeMillis(), (long) postRsList.size());
    }

    PostServiceDetails getDetails(long authorId, int postId, String jwtToken) {
        Person author = getAuthor(authorId);
        List<Like> likes = getLikes(postId).stream().filter(l -> l.getType().equals("Post")).collect(Collectors.toList());
        List<Tag> tags = getTags(postId);
        List<String> tagsStrings = tags.stream().map(Tag::getTag).collect(Collectors.toList());
        Person authUser = getAuthUser(jwtToken);
        List<Comment> postComments = getPostComments(postId);
        List<CommentRs> comments = getComments(postComments, jwtToken);
        comments = comments.stream().filter(c -> c.getParentId() == 0).collect(Collectors.toList());
        return new PostServiceDetails(author, likes, tagsStrings, authUser.getId(), comments);
    }

    public static PostRs setPostRs(Post post2, PostServiceDetails details1) {
        PostRs postRs = PostMapper.INSTANCE.toDTO(post2);
        postRs.setAuthor(PersonMapper.INSTANCE.toDTO(details1.getAuthor()));
        postRs.setComments(details1.getComments());
        postRs.setLikes(details1.getLikes().size());
        postRs.setMyLike(itLikesMe(details1.getLikes(), details1.getAuthUserId()));
        postRs.setTags(details1.getTags());

        return postRs;
    }

    public static boolean itLikesMe(List<Like> likes, long authUserId) {
        for (Like like : likes) {
            if (like.getPersonId().equals(authUserId)) return true;
        }
        return false;
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
            CommentRs commentRs = getCommentRs(author, postComment, subComments, likes, authUserId);
            comments.add(commentRs);
        }
        return comments;
    }

    private CommentRs getCommentRs(Person author, Comment comment, List<CommentRs> subComments, List<Like> likes, long authUserId) {
        CommentRs commentRs = CommentMapper.INSTANCE.toDTO(comment);
        commentRs.setAuthor(PersonMapper.INSTANCE.toDTO(author));
        commentRs.setLikes(likes.size());
        commentRs.setMyLike(itLikesMe(likes, authUserId));
        commentRs.setSubComments(subComments);

        return commentRs;
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
            CommentRs commentRs = getCommentRs(author, parentComment, new ArrayList<>(), likes, authUserId);
            comments.add(commentRs);
        }
        return comments;
    }

    private List<Comment> getSubCommentList(long parentId) {
        return commentRepository.findByPostIdParentId(parentId);
    }

    public CommonRs<PostRs> createPost(PostRq postRq, int id, Integer publishDate, String jwtToken) {
        personRepository.findById((long) id);
        Post post = setPost(postRq, publishDate, id);
        int postId = postRepository.save(post);
        tagRepository.saveAll(postRq.getTags(), postId);
        Person author = personRepository.findById((long) id);
        PostServiceDetails details = getDetails(author.getId(), postId, jwtToken);
        PostRs postRs = setPostRs(post, details);
        return new CommonRs<>(postRs, System.currentTimeMillis());
    }

    private Post setPost(PostRq postRq, Integer publishDate, int id) {
        Post post = PostMapper.INSTANCE.postRqToPost(postRq);
        post.setAuthorId((long) id);
        post.setTime(getTime(publishDate));

        return post;
    }

    Timestamp getTime(Integer publishDate) {
        if (publishDate == null) return new Timestamp(System.currentTimeMillis());
        return new Timestamp(publishDate);
    }

    public CommonRs<PostRs> getPostById(int postId, String jwtToken) {
        Post post = postRepository.findById(postId);

        if (post == null) {
            throw new EntityNotFoundException("Post with id = " + postId + " not found");
        }

        Person author = getAuthor(post.getAuthorId());
        PostServiceDetails details = getDetails(author.getId(), postId, jwtToken);
        PostRs postRs = setPostRs(post, details);
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
        List<Comment> comments = commentRepository.findByPostId((long) id);
        if (comments == null) return new ArrayList<>();
        return commentRepository.findByPostId((long) id);
    }

    public CommonRs<PostRs> updatePost(int id, PostRq postRq, String jwtToken) {
        Post postFromDB = postRepository.findById(id);
        int publishDate = (int) postFromDB.getTime().getTime();
        Post post = setPost(postRq, publishDate, id);
        postRepository.updateById(id, post);
        tagRepository.deleteAll(tagRepository.findByPostId((long) id));
        tagRepository.saveAll(postRq.getTags(), id);
        Post newPost = postRepository.findById(id);
        Person author = getAuthor(newPost.getAuthorId());
        PostServiceDetails details = getDetails(author.getId(), newPost.getId().intValue(), jwtToken);
        PostRs postRs = setPostRs(newPost, details);
        return new CommonRs<>(postRs, System.currentTimeMillis());
    }

    public CommonRs<PostRs> markAsDelete(int id, String jwtToken) {
        Post postFromDB = postRepository.findById(id);
        postFromDB.setIsDeleted(true);
        postFromDB.setTimeDelete(new Timestamp(System.currentTimeMillis()));
        postRepository.markAsDeleteById(id, postFromDB);
        Person author = getAuthor(postFromDB.getAuthorId());
        PostServiceDetails details = getDetails(author.getId(), postFromDB.getId().intValue(), jwtToken);
        PostRs postRs = setPostRs(postFromDB, details);
        return new CommonRs<>(postRs, System.currentTimeMillis());
    }

    public CommonRs<PostRs> recoverPost(int id, String jwtToken) {
        Post postFromDB = postRepository.findById(id);
        postFromDB.setIsDeleted(false);
        Person author = getAuthor(postFromDB.getAuthorId());
        PostServiceDetails details = getDetails(author.getId(), postFromDB.getId().intValue(), jwtToken);
        PostRs postRs = setPostRs(postFromDB, details);
        return new CommonRs<>(postRs, System.currentTimeMillis());
    }

    public void hardDeletingPosts() {
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

    public CommonRs<List<PostRs>> getFeedsByAuthorId(Long id, String jwtToken, Integer offset, Integer perPage) {
        List<Post> postList = postRepository.findPostsByUserId(id);
        postList.sort(Comparator.comparing(Post::getTime).reversed());
        List<PostRs> postRsList = new ArrayList<>();
        for (Post post : postList) {
            int postId = post.getId().intValue();
            PostServiceDetails details = getDetails(post.getAuthorId(), postId, jwtToken);
            PostRs postRs = setPostRs(post, details);
            postRsList.add(postRs);
        }
        int itemPerPage = offset / perPage;
        return new CommonRs<>(postRsList, itemPerPage, offset, perPage, System.currentTimeMillis(), (long) postRsList.size());
    }
}
