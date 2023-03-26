package socialnet.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.mapper.PostMapper;
import socialnet.dto.rs.PostRs;
import java.util.List;

@Repository
public class PostsRepository{
    private final PostMapper postMapper;
    private final JdbcTemplate jdbcTemplate;

    public PostsRepository(PostMapper postMapper, JdbcTemplate jdbcTemplate) {
        this.postMapper = postMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PostRs> getFeeds() {
        return jdbcTemplate.query("SELECT * FROM posts", postMapper);
    }


}
