package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.mapper.TagMapper;
import socialnet.model.db.Tag;

import java.util.List;
@Repository
@RequiredArgsConstructor
public class TagRepository {
    private final TagMapper tagMapper;
    private final JdbcTemplate jdbcTemplate;

    public List<Tag> getTagsByPostId(int postId) {
        return jdbcTemplate.query("SELECT * FROM post2tag WHERE post_id = " + postId, tagMapper);
    }
}
