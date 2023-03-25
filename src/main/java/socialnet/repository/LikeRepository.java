package socialnet.repository;

import org.springframework.jdbc.core.JdbcTemplate;

public class LikeRepository {
    private final JdbcTemplate jdbcTemplate;

    public LikeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
