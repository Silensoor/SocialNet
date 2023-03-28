package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Captcha;

@Repository
@RequiredArgsConstructor
public class CaptchaRepository {
    private final JdbcTemplate jdbcTemplate;

    public int save(Captcha captcha) {
        return jdbcTemplate.update(
            "insert into captcha (code, secret_code, time) values (?, ?, ?)",
            captcha.getCode(),
            captcha.getSecretCode(),
            captcha.getTime()
        );
    }

    public Captcha findBySecretCode(String secretCode) {
        try {
            return jdbcTemplate.queryForObject(
                "select * from captcha where secret_code = ?",
                captchaRowMapper,
                secretCode
            );
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    private final RowMapper<Captcha> captchaRowMapper = (resultSet, rowNum) -> {
        Captcha captcha = new Captcha();
        captcha.setId(resultSet.getLong("id"));
        captcha.setCode(resultSet.getString("code"));
        captcha.setSecretCode(resultSet.getString("secret_code"));
        captcha.setTime(resultSet.getTimestamp("time"));

        return captcha;
    };
}
