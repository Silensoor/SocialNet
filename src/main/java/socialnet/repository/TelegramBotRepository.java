package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TelegramBotRepository {
    private final JdbcTemplate jdbcTemplate;

    public void register(long telegramId, String email, String cmd) {
        jdbcTemplate.update(
            "UPDATE persons SET telegram_id = ? WHERE lower(email) = ?",
            cmd.equals("register") ? telegramId : null,
            email.toLowerCase()
        );
    }
}
