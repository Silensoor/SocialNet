package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Dialog;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DialogsRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Dialog> dialogRowMapper = (rs, rowNum) -> Dialog.builder()
            .id(rs.getLong("id"))
            .firstPersonId(rs.getLong("first_person_id"))
            .secondPersonId(rs.getLong("second_person_id"))
            .lastActiveTime(rs.getTimestamp("last_active_time"))
            .lastMessageId(rs.getLong("last_message_id"))
            .build();

    public List<Dialog> findByAuthorId(Long authorId) {
        return jdbcTemplate.query(
                "SELECT * FROM dialogs WHERE first_person_id = ?",
                dialogRowMapper,
                authorId);
    }


    public List<Dialog> findByRecipientId(Long recipientId) {
        return jdbcTemplate.query(
                "SELECT * FROM dialogs WHERE second_person_id = ?",
                dialogRowMapper,
                recipientId);
    }

    public Dialog findByDialogId(Long dialogId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM dialogs WHERE id = ?",
                    dialogRowMapper,
                    dialogId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public Integer findDialogCount() {
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM dialogs", Integer.class);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }


    public Integer findDialogsUserCount(Integer userId) throws EmptyResultDataAccessException {
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM dialogs WHERE first_person_id = ?" +
                    " OR second_person_id = ?", Integer.class, userId, userId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }
}
