package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.model.Like;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class LikeRepository {

    private final JdbcTemplate jdbcTemplate;

    public Long findCountByPersonId(Long personId) {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM likes WHERE person_id = " + personId, Long.class);
        return count;
    }

    public List<Like> getLikesByEntityId(long postId) {
        String select = "SELECT * FROM likes WHERE entity_id = ?";
        Object[] objects = new Object[]{postId};
        return jdbcTemplate.query(select, objects, new BeanPropertyRowMapper<>(Like.class));
    }
}
