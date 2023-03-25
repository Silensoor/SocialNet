package socialnet.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.mapper.LikeMapper;
import socialnet.model.db.Like;

import java.util.List;

@Repository
@AllArgsConstructor
public class LikeRepository {
    private final LikeMapper likeMapper;
    private final JdbcTemplate jdbcTemplate;

    public List<Like> getLikesByEntityId(long postId) {
        return jdbcTemplate.query("SELECT * FROM likes WHERE entity_id = " + postId, likeMapper);
    }
}
