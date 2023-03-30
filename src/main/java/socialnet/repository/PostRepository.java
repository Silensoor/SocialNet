package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import socialnet.dto.PostRq;
import socialnet.exception.PostException;
import socialnet.exception.RegisterException;
import socialnet.model.Post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Post> findAll() {
        return jdbcTemplate.query("SELECT * FROM posts", new BeanPropertyRowMapper<>(Post.class));
    }

    public int save(Post post) {
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
}
