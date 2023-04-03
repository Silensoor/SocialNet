package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.exception.PostException;
import socialnet.model.Post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Post> findAll() {
        List<Post> posts = jdbcTemplate.query("SELECT * FROM posts", (rs, rowNum) -> {
            Post post = new Post();
            post.setId(rs.getInt("id"));
            post.setIsBlocked(rs.getBoolean("is_blocked"));
            post.setIsDeleted(rs.getBoolean("is_deleted"));
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
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("posts").usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("post_text", post.getPostText());
        values.put("time", post.getTime());
        values.put("title", post.getTitle());
        values.put("author_id", post.getAuthorId());
        values.put("is_blocked", post.getIsBlocked());
        values.put("is_deleted", post.getIsDeleted());

        Number id = simpleJdbcInsert.executeAndReturnKey(values);
        return id.intValue();
    }

    public Post findById(int id) {
        String select = "SELECT * FROM posts WHERE id = " + id;
        List<Post> posts = jdbcTemplate.query(select, new BeanPropertyRowMapper<>(Post.class));
        if (posts.isEmpty()) throw new PostException("Поста с id = " + id + " не существует");
        return posts.get(0);
    }

    public void updateById(int id, Post post) {
        String update = "UPDATE posts SET post_text = \'" + post.getPostText() + "\', title =  \'" + post.getTitle() + "\' WHERE id = " + id;
        jdbcTemplate.update(update);
    }

    public boolean deleteById(int id) {
        String delete = "DELETE FROM posts WHERE id = " + id;
        jdbcTemplate.execute(delete);
        return true;
    }

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
