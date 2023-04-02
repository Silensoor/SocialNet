package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class LikeRepository {

    private final JdbcTemplate jdbcTemplate;

    public Integer findCountByPersonId(Long personId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM likes WHERE person_id = ?", Integer.class, personId);
    }
}
