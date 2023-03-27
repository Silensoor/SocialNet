package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class LikeRepository {

    private final JdbcTemplate jdbcTemplate;

    public Long findCountByPersonId(Long personId) {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM likes WHERE person_id = " + personId, Long.class);
        return count;
    }
}
