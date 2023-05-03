package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
}
