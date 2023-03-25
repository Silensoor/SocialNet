package socialnet.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import socialnet.mapper.PostMapper;
import socialnet.model.rs.PostRs;
import java.util.List;

@Service
public class PostsRepository{
    private final JdbcTemplate jdbcTemplate;

    public PostsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PostRs> getFeeds() {
        return jdbcTemplate.query("SELECT * FROM posts", new PostMapper());
    }


}
