package socialnet.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.model.Like;

import java.util.List;

@Repository
@AllArgsConstructor
public class LikeRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Like> getLikesByEntityId(long postId) {
        String select = "SELECT * FROM likes WHERE entity_id = ?";
        Object[] objects = new Object[]{postId};
        return jdbcTemplate.query(select, objects, new BeanPropertyRowMapper<>(Like.class));
    }
}
