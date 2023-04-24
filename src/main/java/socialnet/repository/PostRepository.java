package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import socialnet.exception.EmptyEmailException;
import socialnet.exception.PostException;
import socialnet.model.Post;
import socialnet.model.Post2Tag;
import socialnet.service.TagService;

import java.sql.Timestamp;
import java.util.*;

@RequiredArgsConstructor
@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PersonRepository personRepository;
    private final TagService tagService;

    public List<Post> findAll() {
        return jdbcTemplate.query("SELECT * FROM posts", postRowMapper);
    }

    public List<Post> findAll(int offset, int perPage) {
        try {
            return jdbcTemplate.query("SELECT * FROM posts WHERE is_deleted = false ORDER BY time DESC OFFSET ? ROWS LIMIT ?", postRowMapper, offset, perPage);
        } catch (EmptyResultDataAccessException ex) {
            return null;
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
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT * FROM posts WHERE id = ?", postRowMapper, id))
                .orElseThrow(() -> new PostException("Поста с id = " + id + " не существует"));
    }

    public void updateById(int id, Post post) {
        String update = "UPDATE posts SET post_text = \'" + post.getPostText() + "\', title =  \'" + post.getTitle() + "\' WHERE id = " + id;
        jdbcTemplate.update(update);
    }

    public void markAsDeleteById(int id, Post post) {
        String update = "UPDATE posts SET is_deleted = " + post.getIsDeleted() + ", time_delete = \'" + post.getTimeDelete() + "\' WHERE id = " + id;
        jdbcTemplate.update(update);
    }

    public boolean deleteById(int id) {
        String delete = "DELETE FROM posts WHERE id = " + id;
        jdbcTemplate.execute(delete);
        return true;
    }

    public List<Post> findPostsByUserId(Long id) {
        return jdbcTemplate.query("Select * from Posts Where id = ?",
                postRowMapper, id);
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

    public List<Post> findDeletedPosts() {
        String select = "SELECT * FROM posts WHERE is_deleted = true";
        return jdbcTemplate.queryForList(select, Post.class);
    }

    public void deleteAll(List<Post> deletingPosts) {
        for (Post deletingPost : deletingPosts) {
            deleteById(deletingPost.getId().intValue());
        }
    }

    public List<Post> findPostStringSql(Integer authorId, Long dateFrom, Long dateTo, String text,
                                        Integer limit, Integer offset, String[] tags, Boolean flagQueryAll) {
        try {
            return jdbcTemplate.query(createSqlPost(authorId, dateFrom, dateTo, text, tags, flagQueryAll),
                    postRowMapper, offset, limit);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
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
        String sql = "";
        if (tags != null) {
            post2TagList = tagService.getPostByQueryTags(tags);
        }
        if (flagQueryAll) {
            sql = "SELECT DISTINCT COUNT(posts.id) FROM posts" +
                    " JOIN post2tag ON posts.id=post2tag.post_id WHERE is_deleted = false AND ";
        } else {
            sql = "SELECT DISTINCT posts.id, posts.is_blocked, posts.is_deleted, posts.post_text," +
                    " posts.time, posts.time_delete, posts.title, posts.author_id FROM posts" +
                    " JOIN post2tag ON posts.id=post2tag.post_id WHERE is_deleted = false AND ";
        }
        if (authorId != null) {
            sql = sql + " author_id = " + authorId + " AND ";
        }
        sql = sql + (dateFrom > 0 ? " time > '" + parseDate(dateFrom) + "' AND " : "");
        sql = sql + (dateTo > 0 ? " time < '" + parseDate(dateTo) + "' AND " : "");
        sql = sql + (!text.equals("") ? " lower (post_text) LIKE '%" + text.toLowerCase() + "%' AND " : "");
        sql = sql + (post2TagList != "" ? " post2tag.tag_id IN (" + post2TagList + ")" : "");
        String str = sql.substring(sql.length() - 5);
        if (str.equals(" AND ")) {
            sql = sql.substring(0, sql.length() - 5);
        }
        if (flagQueryAll) {
            return sql;
        } else {
            return sql + " ORDER BY posts.time DESC OFFSET ? LIMIT ?";
        }
    }

    private Timestamp parseDate(Long str) {
        Date date = new Date(str);
        return new Timestamp(date.getTime());
    }
}
