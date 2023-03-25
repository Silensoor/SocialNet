package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.mapper.PostCommentMapper;
import socialnet.model.rs.CommentRs;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentsRepository {
    private final JdbcTemplate jdbcTemplate;
    private final PostCommentMapper postCommentMapper;

    public List<CommentRs> getCommentsByEntityId(long postId) {
        return jdbcTemplate.query("SELECT * FROM post_comments WHERE post_id = " + postId, postCommentMapper);
    }
}
