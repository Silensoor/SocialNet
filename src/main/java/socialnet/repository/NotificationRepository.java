package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Notification;
import socialnet.model.PersonSettings;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NotificationRepository {
    private final JdbcTemplate jdbcTemplate;

    public void updateIsReadById(Integer notificationId) {
        jdbcTemplate.update("update notifications set is_read = 'true' where id = ?",
                notificationId);
    }

    public void updateIsReadAll(Long id) {
        jdbcTemplate.update("update notifications set is_read = 'true' where person_id = ?", id);
    }

    public List<Notification> getNotificationsById(Integer id) {
        return jdbcTemplate.query("select * from notifications where id = ?",
                notificationRowMapper, id);
    }

    public List<Notification> getNotificationsByPersonId(Long id) {
        return jdbcTemplate.query("select * from notifications where person_id = ? " +
                "and is_read = false order by sent_time", notificationRowMapper, id);
    }

    public void updatePersonSetting(Boolean enable, String typeNotification, Long id) {
        jdbcTemplate.update("update person_settings set " + typeNotification + " =? where id =?", enable, id);
    }

    public List<PersonSettings> getPersonSettings(Long id) {
        return jdbcTemplate.query("select * from person_settings where id =?", personSettingRowMapper, id);
    }

    public List<Notification> getNotifications(Long id, Integer itemPerPage, Integer offset) {
        return jdbcTemplate.query("select * from notifications where person_id = ? " +
                        "and is_read = false order by sent_time",
                notificationRowMapper, id).stream().skip(offset).limit(itemPerPage).collect(Collectors.toList());
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
    private final RowMapper<Notification> notificationRowMapper = (rs, rowNum) -> {
        Notification notification = new Notification();
        notification.setId(rs.getLong("id"));
        notification.setNotificationType(rs.getString("notification_type"));
        notification.setContact(rs.getString("contact"));
        notification.setIsRead(rs.getBoolean("is_read"));
        notification.setPersonId(rs.getLong("person_id"));
        notification.setEntityId(rs.getLong("entity_id"));
        notification.setSentTime(rs.getTimestamp("sent_time"));
        return notification;
    };


    public Long saveNotification(Notification notification) {
        jdbcTemplate.update("insert into notifications(contact,notification_type,entity_id,is_read,sent_time,person_id) values (?,?,?,?,?,?)",
                notification.getContact(), notification.getNotificationType(),
                notification.getEntityId(), notification.getIsRead(),
                notification.getSentTime(), notification.getPersonId());
        return jdbcTemplate.query("select * from notifications where id =(select max(id) from notifications)", (rs, rowNum) -> {
            return rs.getLong("id");
        }).stream().findAny().orElse(null);
    }



}
