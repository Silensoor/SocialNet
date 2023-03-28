package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import socialnet.dto.CommentRs;
import socialnet.dto.PostRs;
import socialnet.mapper.PostCommentMapper;
import socialnet.mapper.PostMapper;
import socialnet.model.*;
import socialnet.repository.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostsService {
    private final PostCommentRepository postCommentRepository;
    private final TagRepository tagRepository;
    private final LikeRepository likeRepository;
    private final PersonRepository personRepository;
    private final PostMapper postMapper;
    private final PostsRepository postsRepository;
    private final PostCommentMapper postCommentMapper;

    public ResponseEntity<String> getFeeds(int offset, int perPage) {
            List<Post> posts = postsRepository.getPosts();
            List<PostRs> postRsList = new ArrayList<>();
            for (Post post : posts) {
                long postId = post.getId();
                Person author = personRepository.getPersonById(post.getAuthorId());
                List<Like> likes = likeRepository.getLikesByEntityId(postId);
                List<Tag> tags = tagRepository.getTagsByPostId(postId);
                long authUserId = 0L;
                List<PostComment> postComments = postCommentRepository.getCommentsByPostId(postId);
                List<CommentRs> comments = getComments(postComments);
                PostRs postRs = postMapper.toDTO(post, author, likes, tags, authUserId, comments);

                System.out.println(postRs.getAuthor());
                System.out.println(postRs.getTags());

                postRsList.add(postRs);
            }


        return null;
    }

    private List<CommentRs> getComments(List<PostComment> postComments) {
        List<CommentRs> comments = new ArrayList<>();
        for (PostComment postComment : postComments) {
            long commentId = postComment.getId();
            Person author = personRepository.getPersonById(postComment.getAuthorId());
            List<PostComment> subCommentsList = postCommentRepository.getCommentsByPostId(commentId);
            List<CommentRs> subComments = getComments(subCommentsList);
            List<Like> likes = likeRepository.getLikesByEntityId(commentId);
            long authUserId = 0;
            CommentRs commentRs = postCommentMapper.toDTO(author, postComment, subComments, likes, authUserId);
            comments.add(commentRs);
        }
        return comments;
    }
}
