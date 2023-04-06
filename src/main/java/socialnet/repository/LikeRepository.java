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

    public Integer findCountByPersonId(Long personId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM likes WHERE person_id = ?", Integer.class, personId);
    }

    public List<Like> getLikesByEntityId(long postId) {
        String select = "SELECT * FROM likes WHERE entity_id = ?";
        Object[] objects = new Object[]{postId};
        return jdbcTemplate.query(select, objects, new BeanPropertyRowMapper<>(Like.class));
    }

    public Boolean isMyLike(String type, Long entityId, Long personId) {
        return jdbcTemplate.queryForObject(
                "Select case when count(*) > 0 then TRUE else FALSE end case from likes\n" +
                        "Where (type = ? )\n" +
                        "  and ((entity_id = ?) and (person_id = ?)) \n",
                new Object[]{type, entityId, personId},
                Boolean.class);
    }
}
