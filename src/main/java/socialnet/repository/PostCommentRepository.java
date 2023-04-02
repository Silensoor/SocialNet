package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.model.PostComment;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostCommentRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<PostComment> getCommentsByPostId(long postId) {
        String select = "SELECT * FROM post_comments WHERE post_id = " + postId;
        return jdbcTemplate.query(select, new BeanPropertyRowMapper<>(PostComment.class));
    }

}
