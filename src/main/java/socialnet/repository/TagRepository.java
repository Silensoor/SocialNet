package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class TagRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Tag> findByPostId(Long postId) {

        return jdbcTemplate.query("SELECT tags.tag FROM tags JOIN post2tag ON tags.id = post2tag.tag_id AND post_id = ?", tagRowMapper, postId);
    }
    public List<Tag> getTagsByPostId(long postId) {
        return jdbcTemplate.queryForList("SELECT * FROM post2tag WHERE post_id = " + postId, Tag.class);
    }
    public long save(Tag tag) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("tags").usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("tag", tag.getTag());

        return simpleJdbcInsert.executeAndReturnKey(values).longValue();
    }

    private final RowMapper<Tag> tagRowMapper = (resultSet, rowNum) -> {
        Tag tag = new Tag();
        tag.setId(resultSet.getLong("id"));
        tag.setTag(resultSet.getString("tag"));

        return tag;
    };

    public void deleteAll(List<Tag> tags) {
        for (Tag tag : tags) {
            String delete = String.format("DELETE FROM tags WHERE id = %d", tag.getId());
            jdbcTemplate.execute(delete);
        }
    }
}
