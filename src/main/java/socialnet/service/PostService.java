package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<CommonRs<List<PostRs>>> getFeeds(String jwtToken, int offset, int perPage) {
            List<Post> posts = postRepository.getPosts();
            List<PostRs> postRsList = new ArrayList<>();
            for (Post post : posts) {
                long postId = post.getId();
                Person author = personRepository.getPersonById(post.getAuthorId());
                List<Like> likes = likeRepository.getLikesByEntityId(postId);
                List<Tag> tags = tagRepository.getTagsByPostId(postId);
                Person authUser = personRepository.findByEmail(jwtUtils.getUserNameFromJwtToken(jwtToken));
                long authUserId = authUser.getId();
                List<PostComment> postComments = postCommentRepository.getCommentsByPostId(postId);
                List<CommentRs> comments = getComments(postComments, jwtToken);
                PostRs postRs = postMapper.toRs(post, author, likes, tags, authUserId, comments);
                postRsList.add(postRs);
            }
            postRsList.sort(Comparator.comparing(PostRs::getTime));
            CommonRs<List<PostRs>> commonRs = new CommonRs<>(postRsList, perPage, offset, perPage, (int) System.currentTimeMillis(), postRsList.size());
        return new ResponseEntity<>(commonRs, HttpStatus.OK);
    }

    private List<CommentRs> getComments(List<PostComment> postComments, String jwtToken) {
        List<CommentRs> comments = new ArrayList<>();
        for (PostComment postComment : postComments) {
            long commentId = postComment.getId();
            Person author = personRepository.getPersonById(postComment.getAuthorId());
            List<PostComment> subCommentsList = postCommentRepository.getCommentsByPostId(commentId);
            List<CommentRs> subComments = getComments(subCommentsList, jwtToken);
            List<Like> likes = likeRepository.getLikesByEntityId(commentId);
            Person authUser = personRepository.findByEmail(jwtUtils.getUserNameFromJwtToken(jwtToken));
            long authUserId = authUser.getId();
            CommentRs commentRs = postCommentMapper.toDTO(author, postComment, subComments, likes, authUserId);
            comments.add(commentRs);
        }
        return comments;
    }

    public ResponseEntity<CommonRs<PostRs>> createPost(PostRq postRq, int id, int publishDate, String jwtToken) {
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
        CommonRs<PostRs> commonRs = new CommonRs<>(postRs, (int) System.currentTimeMillis());
        return new ResponseEntity<>(commonRs, HttpStatus.OK);
    }
}
