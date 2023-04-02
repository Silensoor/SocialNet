package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Post;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Post> findAll() {

        return jdbcTemplate.query("SELECT * FROM posts", postRowMapper);
    }

    private final RowMapper<Post> postRowMapper = (resultSet, rowNum) -> {
        Post post = new Post();
        post.setId(resultSet.getLong("id"));
        post.setIsBlocked(resultSet.getBoolean("is_blocked"));
        post.setIsDeleted(resultSet.getBoolean("is_deleted"));
        post.setPostText(resultSet.getString("post_text"));
        post.setTime(resultSet.getTimestamp("time"));
        post.setTimeDelete(resultSet.getTimestamp("time_delete"));
        post.setTitle(resultSet.getString("title"));
        post.setAuthorId(resultSet.getLong("author_id"));
        return post;
    };
}
