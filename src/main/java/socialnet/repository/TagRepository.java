package socialnet.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import socialnet.model.db.Tag;

import java.util.List;

public class TagRepository {
    private final JdbcTemplate jdbcTemplate;

    public TagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Tag> getTagsByPostId(int postId) {
        return null;
    }
}
