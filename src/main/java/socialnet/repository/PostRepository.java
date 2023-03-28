package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import socialnet.model.Post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public int save(Post post) {
//        String insert = String.format("INSERT INTO posts (post_text, time, time_delete, title, author_id) VALUES(?, ?, ?, ?, ?)",
//                post.getPostText(),
//                post.getTime(),
//                post.getTimeDelete(),
//                post.getTitle(),
//                post.getAuthorId());
//        jdbcTemplate.execute(insert);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("posts").usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("post_text", post.getPostText());
        values.put("time", post.getTime());
        values.put("title", post.getTitle());
        values.put("author_id", post.getAuthorId());
        values.put("is_blocked", post.isBlocked());
        values.put("is_deleted", post.isDeleted());

        Number id = simpleJdbcInsert.executeAndReturnKey(values);
        return id.intValue();
    }
}
