package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
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
        return jdbcTemplate.query("SELECT * FROM posts WHERE is_deleted = false ORDER BY time DESC OFFSET " + offset + " ROWS LIMIT " + perPage, postRowMapper);
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

    public List<Post> findPostStringSql(String author, Long dateFrom, Long dateTo,
                                        String text, Integer limit, Integer offset, String[] tags) {
        try {
            return jdbcTemplate.queryForList(createSqlPost(author, dateFrom, dateTo, text, limit, offset, tags),
                    Post.class);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    private String createSqlPost(String author, Long dateFrom, Long dateTo, String text,
                                 Integer limit, Integer offset, String[] tags) {
        String post2TagList = "";
        if (tags != null) {
            post2TagList = tagService.getPostByQueryTags(tags);
        }
        String sql = "SELECT * FROM posts JOIN post2tag ON posts.id=post2tag.post_id WHERE is_deleted = false ";
        if (author.trim().indexOf(" ") > 0) {
            final Long personId = personRepository.findPersonsName(author);
            sql = sql + (personId != null ? " author_id = " + personId + " AND " : "");
        }
        sql = sql + (dateFrom > 0 ? " time > '" + parseDate(dateFrom) + "' AND " : "");
        sql = sql + (dateTo > 0 ? " time < '" + parseDate(dateTo) + "' AND " : "");
        sql = sql + (!text.equals("") ? " post_text LIKE '%" + text + "%' AND " : "");
        sql = sql + (post2TagList != "" ? " post2tag.tag_id IN (" + post2TagList + ")" : "");
        String str = sql.substring(sql.length() - 5);
        if (str.equals(" AND ")) {
            sql = sql.substring(0, sql.length() - 5);
        }
        return sql + " OFFSET " + offset + " LIMIT " + limit + " ORDER BY time DESC";
    }

    private Timestamp parseDate(Long str) {
        Date date = new Date(str);
        return new Timestamp(date.getTime());
    }

//    public List<Post> findPostStringSql2(List<Post2Tag> post2TagList) {
//        String sql2 = createSqlPost2Tag(post2TagList);
//        if (sql2 != null) {
//            try {
//                return jdbcTemplate.queryForList(sql2, Post.class);
//            } catch (EmptyResultDataAccessException ignored) {
//                return null;
//            }
//        } else {
//            return null;
//        }
//    }
//
//
//    private String createSqlPost2Tag(List<Post2Tag> post2TagList) {
//        StringBuilder sql = new StringBuilder(" ");
//        if (post2TagList != null && !post2TagList.isEmpty()) {
//            for (Post2Tag post2Tag : post2TagList) {
//                if (post2Tag.getPostId() != 0) {
//                    sql.append(" Id = ").append(post2Tag.getId()).append(" OR ");
//                }
//            }
//            if (sql.length() > 4) {
//                if (sql.substring(sql.length() - 4).equals(" OR ")) {
//                    sql.delete(sql.length() - 4, sql.length());
//                }
//            }
//            return sql.toString();
//        } else {
//            return null;
//        }
//    }

}
