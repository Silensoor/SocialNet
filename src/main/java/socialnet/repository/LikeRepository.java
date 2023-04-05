package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import socialnet.model.Like;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Integer save(Like like) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("likes").usingGeneratedKeyColumns("id");

        Map<String, Object> values = new HashMap<>();
        values.put("entity_id", like.getEntityId());
        values.put("time", new Timestamp(System.currentTimeMillis()));
        values.put("person_id", like.getPersonId());
        values.put("type", like.getType());

        Number id = simpleJdbcInsert.executeAndReturnKey(values);
        return id.intValue();
    }

    public void delete(Like like) {
        String delete = String.format("DELETE FROM likes WHERE id = %d", like.getId());
        jdbcTemplate.execute(delete);
    }

    public void deleteAll(List<Like> likes) {
        for (Like like : likes) {
            delete(like);
        }
    }
}
