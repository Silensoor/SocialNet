package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.PersonSettings;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PersonSettingRepository {
    private final JdbcTemplate jdbcTemplate;

    public PersonSettings getPersonSettings(Long id) {
        return jdbcTemplate.query("select * from person_settings where id =?", personSettingRowMapper, id).
                stream().findAny().orElse(null);
    }

    public List<PersonSettings> getSettings(Long personId) {
        return jdbcTemplate.query("Select * from Person_Settings Where Id = ?",
                new BeanPropertyRowMapper<>(PersonSettings.class), personId);
    }

    public void updatePersonSetting(Boolean enable, String typeNotification, Long id) {
        jdbcTemplate.update("update person_settings set " + typeNotification + " =? where id =?", enable, id);
    }
    private final RowMapper<PersonSettings> personSettingRowMapper = (rs, rowNum) -> {
        PersonSettings personSettings = new PersonSettings();
        personSettings.setId(rs.getLong("id"));
        personSettings.setMessageNotification(rs.getBoolean("message_notification"));
        personSettings.setLikeNotification(rs.getBoolean("like_notification"));
        personSettings.setPostNotification(rs.getBoolean("post_notification"));
        personSettings.setFriendBirthdayNotification(rs.getBoolean("friend_birthday_notification"));
        personSettings.setCommentCommentNotification(rs.getBoolean("comment_comment_notification"));
        personSettings.setFriendRequest(rs.getBoolean("friend_request"));
        personSettings.setPostCommentNotification(rs.getBoolean("post_comment_notification"));
        return personSettings;
    };
}
