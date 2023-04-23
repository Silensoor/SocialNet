package socialnet.schedules;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
@RequiredArgsConstructor
public class RemoveOldCaptchasSchedule {
    private final JdbcTemplate jdbcTemplate;

    // Выполнять команду каждый день в 00:00
    @Async
    @Scheduled(cron = "0 0 0 * * *")
    public void removeOldCaptchas() {
        try {
            jdbcTemplate.update("DELETE FROM captcha WHERE time < now() - interval '1 day'");
        } catch (DataAccessException ignored) {
        }
    }
}
