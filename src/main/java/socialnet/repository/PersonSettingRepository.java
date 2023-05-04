package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.api.request.PersonSettingsRq;
import socialnet.model.PersonSettings;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PersonSettingRepository {
    private final JdbcTemplate jdbcTemplate;

    public void insert(String email) {
        jdbcTemplate.update("DO\n" +
                "$$DECLARE new_id bigint;\n" +
                "BEGIN\n" +
                "\tselect id from persons where email = ? into new_id;\n" +
                "\tinsert into person_settings (id) values (new_id);\n" +
                "END$$;\n", email);
    }

    public PersonSettings getPersonSettings(Long id) {
        return jdbcTemplate.query("select * from person_settings where id =?", personSettingRowMapper, id).
                stream().findAny().orElse(null);
    }

    public PersonSettingsRq getSettings(Long personId) {
        return jdbcTemplate.query("Select * from Person_Settings Where Id = ?",
                personSettingRqRowMapper, personId).stream().findAny().orElse(null);
    }

    public void updatePersonSetting(Boolean enable, String typeNotification, Long id) {
        jdbcTemplate.update("update person_settings set " + typeNotification + " =? where id =?", enable, id);
    }

    private final RowMapper<PersonSettingsRq> personSettingRqRowMapper = (rs, rowNum) -> {
        PersonSettingsRq personSettingsRq = new PersonSettingsRq();
        personSettingsRq.setId(rs.getLong("id"));
        personSettingsRq.setMessage(rs.getBoolean("message_notification"));
        personSettingsRq.setPostLike(rs.getBoolean("like_notification"));
        personSettingsRq.setPost(rs.getBoolean("post_notification"));
        personSettingsRq.setFriendBirthday(rs.getBoolean("friend_birthday_notification"));
        personSettingsRq.setCommentComment(rs.getBoolean("comment_comment_notification"));
        personSettingsRq.setFriendRequest(rs.getBoolean("friend_request"));
        personSettingsRq.setPostComment(rs.getBoolean("post_comment_notification"));
        return personSettingsRq;
    };

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
