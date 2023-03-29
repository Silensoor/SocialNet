package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.dto.CommentRs;
import socialnet.dto.CommonRs;
import socialnet.dto.PostRq;
import socialnet.dto.PostRs;
import socialnet.mapper.PostCommentMapper;
import socialnet.mapper.PostMapper;
import socialnet.model.*;
import socialnet.repository.*;
import socialnet.security.jwt.JwtUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostCommentRepository postCommentRepository;
    private final TagRepository tagRepository;
    private final LikeRepository likeRepository;
    private final PersonRepository personRepository;
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostCommentMapper postCommentMapper;
    private final JwtUtils jwtUtils;

    public CommonRs<List<PostRs>> getFeeds(String jwtToken, int offset, int perPage) {
            List<Post> posts = postRepository.findAll();
            List<PostRs> postRsList = new ArrayList<>();
            for (Post post : posts) {
                int postId = (int) post.getId();
                Person author = getAuthor(post.getAuthorId());
                List<Like> likes = getLikes(postId);
                List<Tag> tags = getTags(postId);
                Person authUser = getAuthUser(jwtToken);
                List<PostComment> postComments = getPostComments(postId);
                List<CommentRs> comments = getComments(postComments, jwtToken);
                PostRs postRs = postMapper.toRs(post, author, likes, tags, authUser.getId(), comments);
                postRsList.add(postRs);
            }
            postRsList.sort(Comparator.comparing(PostRs::getTime));
        return new CommonRs<>(postRsList, perPage, offset, perPage, (int) System.currentTimeMillis(), postRsList.size());
    }

    private List<CommentRs> getComments(List<PostComment> postComments, String jwtToken) {
        List<CommentRs> comments = new ArrayList<>();
        for (PostComment postComment : postComments) {
            int commentId = postComment.getId().intValue();
            Person author = getAuthor(postComment.getAuthorId());
            List<PostComment> subCommentsList = getPostComments(commentId);
            List<CommentRs> subComments = getComments(subCommentsList, jwtToken);
            List<Like> likes = getLikes(commentId);
            Person authUser = personRepository.findByEmail(jwtUtils.getUserNameFromJwtToken(jwtToken));
            long authUserId = authUser.getId();
            CommentRs commentRs = postCommentMapper.toDTO(author, postComment, subComments, likes, authUserId);
            comments.add(commentRs);
        }
        return comments;
    }

    public CommonRs<PostRs> createPost(PostRq postRq, int id, int publishDate, String jwtToken) {
        Post post = postMapper.toModel(postRq, publishDate, id);
        int postId = postRepository.save(post);
        List<Like> likes = likeRepository.getLikesByEntityId(postId);
        List<Tag> tags = tagRepository.getTagsByPostId(postId);
//        Person authUser = personRepository.findByEmail(jwtUtils.getUserNameFromJwtToken(jwtToken));
//        long authUserId = authUser.getId();
        long authUserId = 1;
        List<PostComment> postComments = postCommentRepository.getCommentsByPostId(postId);
        List<CommentRs> comments = getComments(postComments, jwtToken);
        PostRs postRs = postMapper.toRs(post, personRepository.getPersonById(id), likes, tags, authUserId, comments);
        return new CommonRs<>(postRs, (int) System.currentTimeMillis());
    }

    public CommonRs<PostRs> getPostById(int postId, String jwtToken) {
        Post post = postRepository.findById(postId);
        Person author = getAuthor(post.getAuthorId());
        List<Like> likes = getLikes(postId);
        List<Tag> tags = getTags(postId);
        long authUserId = getAuthUser(jwtToken).getId();
        List<PostComment> postComments = getPostComments(postId);
        List<CommentRs> comments = getComments(postComments, jwtToken);
        PostRs postRs = postMapper.toRs(post, author, likes, tags, authUserId, comments);
        return new CommonRs<>(postRs, (int) System.currentTimeMillis());
    }

    private Person getAuthor(long id) {
        return personRepository.getPersonById(id);
    }
    private List<Like> getLikes(int id) {
        return likeRepository.getLikesByEntityId(id);
    }
    private List<Tag> getTags(int id) {
        return tagRepository.getTagsByPostId(id);
    }
    private Person getAuthUser(String jwtToken) {
//        return personRepository.findByEmail(jwtUtils.getUserNameFromJwtToken(jwtToken));

        Person person = new Person();
        person.setId(1L);
        return person;
    }
    private List<PostComment> getPostComments(int id) {
        return postCommentRepository.getCommentsByPostId(id);
    }

    public CommonRs<PostRs> updatePost(int id, PostRq postRq, String jwtToken) {
        Post postFromDB = postRepository.findById(id);
        int publishDate = (int) postFromDB.getTime().getTime();
        Post post = postMapper.toModel(postRq, publishDate, id);
        postRepository.updateById(id, post);
        Post newPost = postRepository.findById(id);
        Person author = getAuthor(newPost.getId());
        List<Like> likes = getLikes((int) newPost.getId());
        List<Tag> tags = getTags((int) newPost.getId());
        List<PostComment> postComments = getPostComments((int) newPost.getId());
        List<CommentRs> comments = getComments(postComments, jwtToken);
        PostRs postRs = postMapper.toRs(newPost, author, likes, tags, getAuthUser(jwtToken).getId(), comments);
        return new CommonRs<>(postRs, (int) System.currentTimeMillis());
    }

    public CommonRs<PostRs> markAsDelete(int id, String jwtToken) {
        Post postFromDB = postRepository.findById(id);
        postFromDB.setDeleted(true);
        postRepository.updateById(id, postFromDB);
        Person author = getAuthor(postFromDB.getId());
        List<Like> likes = getLikes((int) postFromDB.getId());
        List<Tag> tags = getTags((int) postFromDB.getId());
        List<PostComment> postComments = getPostComments((int) postFromDB.getId());
        List<CommentRs> comments = getComments(postComments, jwtToken);
        PostRs postRs = postMapper.toRs(postFromDB, author, likes, tags, getAuthUser(jwtToken).getId(), comments);
        return new CommonRs<>(postRs,(int) System.currentTimeMillis());
    }

    public CommonRs<PostRs> recoverPost(int id, String jwtToken) {
        Post postFromDB = postRepository.findById(id);
        postFromDB.setDeleted(false);
        Person author = getAuthor(postFromDB.getId());
        List<Like> likes = getLikes((int) postFromDB.getId());
        List<Tag> tags = getTags((int) postFromDB.getId());
        List<PostComment> postComments = getPostComments((int) postFromDB.getId());
        List<CommentRs> comments = getComments(postComments, jwtToken);
        PostRs postRs = postMapper.toRs(postFromDB, author, likes, tags, getAuthUser(jwtToken).getId(), comments);
        return new CommonRs<>(postRs,(int) System.currentTimeMillis());
    }
}
