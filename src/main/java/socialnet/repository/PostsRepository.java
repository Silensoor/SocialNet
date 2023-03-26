package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.mapper.PostMapper;
import socialnet.dto.PostRs;
import socialnet.model.Post;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostsRepository{
    private final PostMapper postMapper;
    private final JdbcTemplate jdbcTemplate;

    public List<PostRs> getFeeds() {
        List<Post> posts = jdbcTemplate.query("SELECT * FROM posts", Post.class);
        List<PostRs> postRsList = new ArrayList<>();
        for (Post post : posts) {
            PostRs postRs = postMapper.toDTO(post);
            postRsList.add(postRs);
        }
        return postRsList;
    }


}
