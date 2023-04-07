package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Post2Tag;
import socialnet.model.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class TagRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Tag> findByPostId(Long postId) {
        List<Tag> tags = jdbcTemplate.query("SELECT * FROM tags JOIN post2tag ON tags.id = post2tag.tag_id AND post_id = ?", tagRowMapper, postId);
        return tags.stream().filter(t -> t.getTag() != null).collect(Collectors.toList());
    }
    public List<Tag> getTagsByPostId(long postId) {

        return jdbcTemplate.queryForList("SELECT * FROM post2tag WHERE post_id = " + postId, Tag.class);
    }
    public long save(Tag tag, long postId) {
        SimpleJdbcInsert simpleJdbcInsert1 = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert1.withTableName("tags").usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("tag", tag.getTag());
        Map<String, Object> valuesPost2Tags = new HashMap<>();
        Long tagId = simpleJdbcInsert1.executeAndReturnKey(values).longValue();
        SimpleJdbcInsert simpleJdbcInsert2 = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert2.withTableName("post2tag").usingGeneratedKeyColumns("id");
        valuesPost2Tags.put("tag_id", tagId);
        valuesPost2Tags.put("post_id", postId);
        simpleJdbcInsert2.execute(valuesPost2Tags);
        return tagId;
    }

    private final RowMapper<Tag> tagRowMapper = (resultSet, rowNum) -> {
        Tag tag = new Tag();
        while (resultSet.next()) {
            tag.setId(resultSet.getLong("id"));
            tag.setTag(resultSet.getString("tag"));
        }
        return tag;
    };

    public void deleteAll(List<Tag> tags) {
        for (Tag tag : tags) {
            String delete = String.format("DELETE FROM tags WHERE id = %d", tag.getId());
            jdbcTemplate.execute(delete);
        }
    }

    public void saveAll(List<String> tags, long postId) {
        for (String tag : tags) {
            save(new Tag(tag), postId);
        }
    }
}
