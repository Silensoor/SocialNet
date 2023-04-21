package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Message;

import java.util.List;

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

    public Message findLastMessageByDialogId(Long dialogId) {
        return jdbcTemplate.queryForObject(
                        "SELECT * FROM messages WHERE dialog_id = ? ORDER BY time DESC LIMIT 1;",
                        messageRowMapper,
                        dialogId);
    }

    public List<Message> findByDialogId(Long dialogId, Integer itemPerPage) {
        return jdbcTemplate.query("SELECT * FROM messages WHERE dialog_id = ? AND is_deleted = false LIMIT ?",
                messageRowMapper,
                dialogId, itemPerPage);
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

    public Integer updateReadStatusByDialogId(Long dialogId, String readStatus) {
        return jdbcTemplate.update("UPDATE messages SET read_status = ? WHERE dialog_id = ?", readStatus, dialogId);
    }

    public int save(Message message) {
        return jdbcTemplate.update("INSERT INTO messages (is_deleted, " +
                            "message_text, " +
                            "read_status, " +
                            "time, " +
                            "dialog_id, " +
                            "author_id, " +
                            "recipient_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)",
                message.getIsDeleted(),
                message.getMessageText(),
                message.getReadStatus(),
                message.getTime(),
                message.getDialogId(),
                message.getAuthorId(),
                message.getRecipientId());
    }

    public void markDeleted(Long messageId, Boolean isDeletedState) {
        jdbcTemplate.update("UPDATE messages SET is_deleted = ? WHERE id = ?", isDeletedState, messageId);
    }

    public Integer updateTextById(String text, Long messageId) {
        return jdbcTemplate.update("UPDATE messages SET message_text = ? WHERE id = ?", text, messageId);
    }
}
