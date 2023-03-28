package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.mapper.PostCommentMapper;
import socialnet.dto.CommentRs;
import socialnet.model.PostComment;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentsRepository {
    private final JdbcTemplate jdbcTemplate;
    private final PostCommentMapper postCommentMapper;

    public List<PostComment> getCommentsByEntityId(long postId) {
        return jdbcTemplate.query("SELECT * FROM post_comments WHERE post_id = " + postId, new BeanPropertyRowMapper<>(PostComment.class));
    }
}