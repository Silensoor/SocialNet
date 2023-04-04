package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import socialnet.model.Post2Tag;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class Post2TagRepository {
    private final JdbcTemplate jdbcTemplate;

    public void save(Post2Tag post2Tag) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("tags").usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("tag_id", post2Tag.getTagId());
        values.put("post_id", post2Tag.getPostId());

        simpleJdbcInsert.execute(values);
    }
}
