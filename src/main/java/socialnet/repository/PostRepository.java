package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.model.Post;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Post> getPosts() {
        return jdbcTemplate.query("SELECT * FROM posts", new BeanPropertyRowMapper<>(Post.class));
    }

    public List<Post> findAll() {
        List<Post> posts = jdbcTemplate.query("SELECT * FROM posts", (rs, rowNum) -> {
            Post post = new Post();
            post.setId(rs.getInt("id"));
            post.setBlocked(rs.getBoolean("is_blocked"));
            post.setDeleted(rs.getBoolean("is_deleted"));
            post.setPostText(rs.getString("post_text"));
            post.setTime(rs.getTimestamp("time"));
            post.setTimeDelete(rs.getTimestamp("time_delete"));
            post.setTitle(rs.getString("title"));
            post.setAuthorId(rs.getInt("author_id"));
            return post;
        });

        return posts;
    }
}
