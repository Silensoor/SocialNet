package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class TagRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<String> findByPostId(Integer postId) {
        List<String> result = jdbcTemplate.query("SELECT tags.tag FROM tags JOIN post2tag ON tags.id = post2tag.tag_id AND post_id = " + postId, (rs, rowNum) -> {
            String tag = rs.getString("tag");
            return tag;
        });

        return result;
    }
}
