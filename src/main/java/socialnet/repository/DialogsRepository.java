package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Dialog;

import java.util.List;
import java.util.Optional;

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
        return Optional.of(jdbcTemplate.query(
                        "SELECT * FROM dialogs WHERE first_person_id = ?",
                        dialogRowMapper,
                        authorId))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new RuntimeException("Диалога с автором = " + authorId + " не существует"));
    }
}
