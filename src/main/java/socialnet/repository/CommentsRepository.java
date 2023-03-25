package socialnet.repository;

import org.springframework.jdbc.core.JdbcTemplate;

public class CommentsRepository {
    private final JdbcTemplate jdbcTemplate;

    public CommentsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


}
