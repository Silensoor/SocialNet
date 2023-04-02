package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Tag;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class TagRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Tag> findByPostId(Long postId) {

        return jdbcTemplate.query("SELECT tags.tag FROM tags JOIN post2tag ON tags.id = post2tag.tag_id AND post_id = ?", tagRowMapper, postId);
    }

    private final RowMapper<Tag> tagRowMapper = (resultSet, rowNum) -> {
        Tag tag = new Tag();
        tag.setId(resultSet.getLong("id"));
        tag.setTag(resultSet.getString("tag"));

        return tag;
    };
}
