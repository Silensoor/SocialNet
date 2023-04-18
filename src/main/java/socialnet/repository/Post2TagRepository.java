package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import socialnet.model.Post2Tag;
import socialnet.model.Tag;

import java.util.HashMap;
import java.util.List;
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

    public List<Tag> findTagsByPostId(Long postId) {
        return jdbcTemplate.query(
                "Select t.id, t.ta  g From post2tag p\n" +
                        "join tags t on (p.post_id = t.id)\n" +
                        "Where p.post_id = ?",
                new Object[]{postId},
                new BeanPropertyRowMapper<>(Tag.class));
    }

    public List<Post2Tag> getQuery(List<Tag> tagList) {
        StringBuilder sql1 = new StringBuilder("SELECT * FROM post2tag WHERE");
        for (Tag tag1 : tagList) {
            sql1.append(" tag_id = ").append(tag1.getId()).append(" AND ");
        }
        if (sql1.substring(sql1.length() - 5).equals(" AND ")){
            sql1 = new StringBuilder(sql1.substring(0, sql1.length() - 5));
        }
        if (!sql1.toString().equals("SELECT * FROM post2tag WHERE")) {
            try {
                return this.jdbcTemplate.query(sql1.toString(), post2TagRowMapper);
            } catch (EmptyResultDataAccessException ignored) {
                return null;
            }
        } else {
            return null;
        }
    }

    private final RowMapper<Post2Tag> post2TagRowMapper = (resultSet, rowNum) -> {
        Post2Tag post2Tag = new Post2Tag();
        post2Tag.setId(resultSet.getLong("id"));
        post2Tag.setPostId(resultSet.getLong("post_id"));
        post2Tag.setTagId(resultSet.getLong("tag_id"));
        return post2Tag;
    };
}
