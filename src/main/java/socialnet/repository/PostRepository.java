package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import socialnet.model.Post;
import socialnet.service.TagService;

import java.sql.Timestamp;
import java.util.*;

@RequiredArgsConstructor
@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TagService tagService;

    public List<Post> findAll() {
        try {
            return jdbcTemplate.query("SELECT * FROM posts", postRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    public List<Post> findAll(int offset, int perPage) {
        try {
            return jdbcTemplate.query("SELECT * FROM posts WHERE is_deleted = false ORDER BY time DESC OFFSET ? ROWS LIMIT ?", postRowMapper, offset, perPage);
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    public List<Post> findAll(int offset, int perPage, long currentTime) {
        try {
            return jdbcTemplate.query("SELECT * FROM posts WHERE is_deleted = false AND time < ? ORDER BY time DESC OFFSET ? ROWS LIMIT ?", postRowMapper, new Timestamp(currentTime), offset, perPage);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public long getAllCount() {
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(1) FROM posts WHERE is_deleted = false", Long.class);
        } catch (EmptyResultDataAccessException ignored) {
            return 0L;
        }
    }

    public int save(Post post) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("posts").usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("post_text", post.getPostText());
        values.put("time", post.getTime());
        values.put("title", post.getTitle());
        values.put("author_id", post.getAuthorId());
        values.put("is_blocked", post.getIsBlocked());
        values.put("is_deleted", post.getIsDeleted());

        Number id = simpleJdbcInsert.executeAndReturnKey(values);
        return id.intValue();
    }

    public Post findById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM posts WHERE id = ?",
                    postRowMapper,
                    id
            );
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public void updateById(int id, Post post) {
        String update = "UPDATE posts SET post_text = ?, title = ? WHERE id = ?";
        jdbcTemplate.update(update, post.getPostText(), post.getTitle(), id);
    }

    public void markAsDeleteById(int id) {
        String update = "UPDATE posts SET is_deleted = true, time_delete = now() WHERE id = ?";
        jdbcTemplate.update(update, id);
    }

    public boolean deleteById(int id) {
        String delete = "DELETE FROM posts WHERE id = ?";
        jdbcTemplate.update(delete, id);
        return true;
    }

    public List<Post> findPostsByUserId(Long userId, Integer offset, Integer perPage) {
        try {
            return jdbcTemplate.query(
                    "select * from posts where author_id = ? order by time desc offset ? rows limit ?",
                    postRowMapper,
                    userId,
                    offset,
                    perPage
            );
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }

    public Long countPostsByUserId(Long userId) {
        try {
            return jdbcTemplate.queryForObject(
                "select count(1) from posts where author_id = ?",
                Long.class,
                userId
            );
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<Post> findDeletedPosts() {
        String select = "SELECT * FROM posts WHERE is_deleted = true";
        try{
            return jdbcTemplate.query(select, postRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    public List<Post> findPostStringSql(Integer authorId, Long dateFrom, Long dateTo, String text,
                                        Integer limit, Integer offset, String[] tags, Boolean flagQueryAll) {
        try {
            return jdbcTemplate.query(createSqlPost(authorId, dateFrom, dateTo, text, tags, flagQueryAll),
                    postRowMapper, offset, limit);
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }

    public Integer findPostStringSqlAll(Integer authorId, Long dateFrom, Long dateTo,
                                        String text, String[] tags, Boolean flagQueryAll) {
        try {
            return jdbcTemplate.queryForObject(createSqlPost(authorId, dateFrom, dateTo, text, tags, flagQueryAll),
                    Integer.class);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    private String createSqlPost(Integer authorId, Long dateFrom, Long dateTo,
                                 String text, String[] tags, Boolean flagQueryAll) {
        String post2TagList = "";
        StringBuilder sql = new StringBuilder();
        if (Boolean.TRUE.equals(flagQueryAll)) {
            sql.append("SELECT DISTINCT COUNT(posts.id) FROM posts");
        } else {
            sql.append("SELECT DISTINCT posts.id, posts.is_blocked, posts.is_deleted, posts.post_text," +
                    " posts.time, posts.time_delete, posts.title, posts.author_id FROM posts");
        }
        if (tags != null) {
            post2TagList = tagService.getPostByQueryTags(tags);
            sql.append("JOIN post2tag ON posts.id=post2tag.post_id");
        }
        sql.append(" WHERE is_deleted = false AND ");
        if (authorId != null) {
            sql.append(" author_id = ").append(authorId).append(" AND ");
        }
        sql.append(dateFrom > 0 ? " time > '" + parseDate(dateFrom) + "' AND " : "");
        sql.append(dateTo > 0 ? " time < '" + parseDate(dateTo) + "' AND " : "");
        sql.append(post2TagList != "" ? " post2tag.tag_id IN (" + post2TagList + ")  AND " : "");
        if (text.equals("'")){
            text = "\"";
        }
        sql.append(!text.equals("") ? " lower (post_text) LIKE '%" + text.toLowerCase() + "%'" : "");
        String str = sql.substring(sql.length() - 5);
        String sql1;
        if (str.equals(" AND ")) {
            sql1 = sql.substring(0, sql.length() - 5);
        } else {
            sql1 = sql.toString();
        }
        if (Boolean.TRUE.equals(flagQueryAll)) {
            return sql1;
        } else {
            return sql1 + " ORDER BY posts.time DESC OFFSET ? LIMIT ?";
        }
    }

    private Timestamp parseDate(Long str) {
        Date date = new Date(str);
        return new Timestamp(date.getTime());
    }

    private final RowMapper<Post> postRowMapper = (resultSet, rowNum) -> {
        Post post = new Post();
        post.setId(resultSet.getLong("id"));
        post.setIsBlocked(resultSet.getBoolean("is_blocked"));
        post.setIsDeleted(resultSet.getBoolean("is_deleted"));
        post.setPostText(resultSet.getString("post_text"));
        post.setTime(resultSet.getTimestamp("time"));
        post.setTimeDelete(resultSet.getTimestamp("time_delete"));
        post.setTitle(resultSet.getString("title"));
        post.setAuthorId(resultSet.getLong("author_id"));
        return post;
    };


    public Integer getAllPostByUser(Integer userId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM posts WHERE author_id = ?",
                Integer.class, userId);
    }
}
