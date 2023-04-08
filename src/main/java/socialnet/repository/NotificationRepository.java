package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Notification;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Notification> getNotificationsByPersonIdAndNotificationType(Long personId, String notificationType) {
        return jdbcTemplate.query("select * from notifications as n where n.person_id=? and n.notification_type=?",
                new Object[]{personId, notificationType}, notifications);
    }

    public void updateNotification(Boolean isRead, Long notificationId) {
        jdbcTemplate.update("update notifications set is_read=? where id=?",
                isRead, notificationId);
    }


    private final RowMapper<Notification> notifications = (resultSet, rowNum) -> {
        Notification notification = new Notification();
        notification.setId(resultSet.getLong("id"));
        notification.setNotificationType(resultSet.getString("notification_type"));
        notification.setContact(resultSet.getString("contact"));
        notification.setIsRead(resultSet.getBoolean("id_read"));
        notification.setPersonId(resultSet.getLong("person_id"));
        notification.setEntityId(resultSet.getLong("entity_id"));
        notification.setSentTime(resultSet.getTimestamp("sent_time"));
        return notification;
    };
}
