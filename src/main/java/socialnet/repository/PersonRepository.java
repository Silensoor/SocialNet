package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.model.Person;

@RequiredArgsConstructor
@Repository
public class PersonRepository {

    private final JdbcTemplate jdbcTemplate;

    public Person findById(Integer authorId) {
        Person author = jdbcTemplate.queryForObject("SELECT * FROM persons WHERE id = " + authorId, (rs, rowNum) -> {
            Person person = new Person();
            person.setAbout(rs.getString("about"));
            person.setBirthDate(rs.getTimestamp("birth_date"));
            person.setChangePasswordToken(rs.getString("change_password_token"));
            person.setConfigurationCode(rs.getInt("configuration_code"));
            person.setDeletedTime(rs.getTimestamp("deleted_time"));
            person.setEmail(rs.getString("email"));
            person.setFirstName(rs.getString("first_name"));
            person.setIsApproved(rs.getBoolean("is_approved"));
            person.setIsBlocked(rs.getBoolean("is_blocked"));
            person.setIsDeleted(rs.getBoolean("is_deleted"));
            person.setLastName(rs.getString("last_name"));
            person.setLastOnlineTime(rs.getTimestamp("last_online_time"));
            person.setMessagePermissions(rs.getString("message_permissions"));
            person.setNotificationsSessionId(rs.getString("notifications_session_id"));
            person.setOnlineStatus(rs.getString("online_status"));
            person.setPassword(rs.getString("password"));
            person.setPhone(rs.getString("phone"));
            person.setPhoto(rs.getString("photo"));
            person.setRegDate(rs.getTimestamp("reg_date"));
            person.setCity(rs.getString("city"));
            person.setCountry(rs.getString("country"));
            person.setTelegramId(rs.getLong("telegram_id"));
            person.setPersonSettingsId(rs.getLong("person_settings_id"));

            return person;
        });

        return author;
    }
}
