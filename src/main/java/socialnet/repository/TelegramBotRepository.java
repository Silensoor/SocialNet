package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.api.response.TgNotificationFromRs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class TelegramBotRepository {
    private final JdbcTemplate jdbcTemplate;

    public boolean register(long telegramId, String email, String cmd) {
        int cnt = jdbcTemplate.queryForObject(
            "select count(1) from persons where lower(email) = ? and telegram_id is not null",
            Integer.class,
            email.toLowerCase()
        );

        if (cnt > 0) {
            return false;
        }

        jdbcTemplate.update(
            "UPDATE persons SET telegram_id = ? WHERE lower(email) = ?",
            cmd.equals("register") ? telegramId : null,
            email.toLowerCase()
        );

        return true;
    }

    public String getFullName(long telegramId) {
        return jdbcTemplate.queryForObject(
            "select first_name || ' ' || last_name from persons where telegram_id = ?",
            String.class,
            telegramId
        );
    }

    public boolean unregister(long telegramId) {
        try {
            jdbcTemplate.update(
                "UPDATE persons SET telegram_id = null WHERE telegram_id = ?",
                telegramId
            );
        } catch (Exception ignored) {
            return false;
        }

        return true;
    }

    public Map<String, List<TgNotificationFromRs>> getNotifications(String listUserId) {
        Map<String, List<TgNotificationFromRs>> result = new HashMap<>();

        jdbcTemplate.query(
            "SELECT n.person_id, " +
            "       n.notification_type, " +
            "       p.first_name || ' ' || p.last_name as name " +
            "  FROM notifications n, " +
            "       persons p " +
            " WHERE p.id = n.entity_id " +
            "   AND n.is_read = false " +
            "   AND n.person_id IN (" + listUserId + ") " +
            " GROUP BY n.person_id, " +
            "          n.notification_type, " +
            "          name " +
            " ORDER BY n.person_id",
            (rs, rowNum) -> {
                TgNotificationFromRs n = TgNotificationFromRs.builder()
                    .from(String.valueOf(rs.getLong(3)))
                    .type(rs.getString(2))
                    .build();

                String to = String.valueOf(rs.getLong(1));

                if (result.get(to) == null) {
                    List<TgNotificationFromRs> notifications = new ArrayList<>();
                    notifications.add(n);
                    result.put(to, notifications);
                } else {
                    result.get(to).add(n);
                }

                return n;
            }
        );

        return result;
    }
}
