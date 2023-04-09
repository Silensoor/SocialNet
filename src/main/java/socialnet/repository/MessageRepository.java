package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Message;

@Repository
@RequiredArgsConstructor
public class MessageRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Message> messageRowMapper = (rs, rowNum) -> Message.builder()
            .id(rs.getLong("id"))
            .isDeleted(rs.getBoolean("is_deleted"))
            .messageText(rs.getString("message_text"))
            .readStatus(rs.getString("read_status"))
            .time(rs.getTimestamp("time"))
            .dialogId(rs.getLong("dialog_id"))
            .authorId(rs.getLong("author_id"))
            .recipientId(rs.getLong("recipient_id"))
            .build();

    public Message findByDialogId(Long dialogId) {
        return jdbcTemplate.queryForObject(
                        "SELECT * FROM messages WHERE dialog_id = ? ORDER BY time DESC LIMIT 1;",
                        messageRowMapper,
                        dialogId);
    }

    public Long findCountByDialogIdAndReadStatus(Long dialogId, String readStatus) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM messages WHERE dialog_id = ? AND read_status = ?",
                Long.class,
                dialogId, readStatus);
    }

    public Message findByAuthorId(Long authorId) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM messages WHERE author_id = ? ORDER BY time DESC LIMIT 1;",
                messageRowMapper,
                authorId);
    }

    public Long findCountByAuthorIdAndReadStatus(Long authorId, String readStatus) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM messages WHERE author_id = ? AND read_status = ?",
                Long.class,
                authorId, readStatus);
    }
}
