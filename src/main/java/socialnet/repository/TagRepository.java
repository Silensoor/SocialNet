package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.model.Tag;

import java.util.List;
@Repository
@RequiredArgsConstructor
public class TagRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Tag> getTagsByPostId(int postId) {
        return jdbcTemplate.queryForList("SELECT * FROM post2tag WHERE post_id = " + postId, Tag.class);
    }
}
