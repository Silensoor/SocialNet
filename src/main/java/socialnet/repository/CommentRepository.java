package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.model.Comment;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Comment> findByPostId(Long postId) {
        List<Comment> result = jdbcTemplate.query("SELECT * FROM post_comments WHERE post_id = " + postId, (rs, rowNum) -> {
            Comment comment = new Comment();
            comment.setId(rs.getLong("id"));
            comment.setCommentText(rs.getString("comment_text"));
            comment.setIsBlocked(rs.getBoolean("is_blocked"));
            comment.setIsDeleted(rs.getBoolean("is_deleted"));
            comment.setTime(rs.getTimestamp("time"));
            comment.setParentId(rs.getLong("parent_id"));
            comment.setAuthorId(rs.getLong("author_id"));
            comment.setPostId(rs.getLong("post_id"));
            return comment;
        });

        return result;
    }
}
