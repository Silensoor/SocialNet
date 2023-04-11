package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import socialnet.model.Comment;
import socialnet.model.Post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public long save(Comment comment) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("post_comments").usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("comment_text", comment.getCommentText());
        values.put("is_blocked", comment.getIsBlocked());
        values.put("is_deleted", comment.getIsDeleted());
        values.put("time", comment.getTime());
        values.put("parent_id", comment.getParentId());
        values.put("author_id", comment.getAuthorId());
        values.put("post_id", comment.getPostId());

        Number id = simpleJdbcInsert.executeAndReturnKey(values);
        return id.intValue();


    }

    public Comment findById(Long commentId) {
        String select = "SELECT * FROM post_comments WHERE id = " + commentId;
        List<Comment> comments = jdbcTemplate.query(select, (rs, rowNum) -> {
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

        return comments.get(0);
    }

    public void updateById(Comment comment, Long commentId) {
        String update = "UPDATE post_comments SET comment_text = \'" + comment.getCommentText() + "\', is_deleted = \'" + comment.getIsDeleted() + "\' WHERE id = " + commentId;
        jdbcTemplate.execute(update);
    }

    public List<Comment> findDeletedPosts() {
        String select = "SELECT * FROM post_comments WHERE is_deleted = true";
        List<Comment> deletedComments = jdbcTemplate.query(select, (rs, rowNum) -> {
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
        return deletedComments;
    }

    public void delete(Comment comment) {
        String delete = "DELETE FROM post_comments WHERE id = " + comment.getId();
        jdbcTemplate.execute(delete);
    }

    public void deleteAll(List<Comment> deletingComments) {
        for (Comment deletingComment : deletingComments) {
            delete(deletingComment);
        }
    }
}
