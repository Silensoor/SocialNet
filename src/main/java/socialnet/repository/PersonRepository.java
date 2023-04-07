package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.exception.PostException;
import socialnet.model.Person;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class PersonRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(Person person) {
        jdbcTemplate.update(
                "INSERT INTO persons " +
                        "(email, first_name, last_name, password, reg_date, is_approved, is_blocked, is_deleted) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                person.getEmail(),
                person.getFirstName(),
                person.getLastName(),
                person.getPassword(),
                person.getRegDate(),
                person.getIsApproved(),
                person.getIsBlocked(),
                person.getIsDeleted()
        );
    }

    public Person findByEmail(String email) {
        return jdbcTemplate.queryForObject("SELECT * FROM persons WHERE email = ?", personRowMapper, email);
    }

    public Person findById(Long authorId) {
        String select = "SELECT * FROM persons WHERE id = " + authorId;
        List<Person> personList = jdbcTemplate.query(select, new BeanPropertyRowMapper<>(Person.class));
        if (personList.isEmpty()) throw new PostException("Person с id " + authorId + " не существует");
        return personList.get(0);
    }

    public List<Person> findPersonAll(Long limit) {
        try {
            return this.jdbcTemplate.query("SELECT * FROM persons LIMIT ?", new Object[]{limit}, personRowMapper);

        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<Person> findPersonFriendsAll(String sql) {
        try {
            return this.jdbcTemplate.query(sql, personRowMapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<Person> findPersonsEmail(String email) {
        try {
            return this.jdbcTemplate.query("SELECT * FROM persons WHERE email = ?",
                    new Object[]{email}, personRowMapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<Person> findPersonsCity(String city) {
        try {
            return this.jdbcTemplate.query("SELECT * FROM persons WHERE city = ?",
                    new Object[]{city}, personRowMapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    private final RowMapper<Person> personRowMapper = (resultSet, rowNum) -> {
        Person person = new Person();
        person.setId(resultSet.getLong("id"));
        person.setAbout(resultSet.getString("about"));
        person.setBirthDate(resultSet.getTimestamp("birth_date"));
        person.setChangePasswordToken(resultSet.getString("change_password_token"));
        person.setConfigurationCode(resultSet.getInt("configuration_code"));
        person.setDeletedTime(resultSet.getTimestamp("deleted_time"));
        person.setEmail(resultSet.getString("email"));
        person.setFirstName(resultSet.getString("first_name"));
        person.setIsApproved(resultSet.getBoolean("is_approved"));
        person.setIsBlocked(resultSet.getBoolean("is_blocked"));
        person.setIsDeleted(resultSet.getBoolean("is_deleted"));
        person.setLastName(resultSet.getString("last_name"));
        person.setLastOnlineTime(resultSet.getTimestamp("last_online_time"));
        person.setMessagePermissions(resultSet.getString("message_permissions"));
        person.setNotificationsSessionId(resultSet.getString("notifications_session_id"));
        person.setOnlineStatus(resultSet.getString("online_status"));
        person.setPassword(resultSet.getString("password"));
        person.setPhone(resultSet.getString("phone"));
        person.setPhoto(resultSet.getString("photo"));
        person.setRegDate(resultSet.getTimestamp("reg_date"));
        person.setCity(resultSet.getString("city"));
        person.setCountry(resultSet.getString("country"));
        person.setTelegramId(resultSet.getLong("telegram_id"));
        person.setPersonSettingsId(resultSet.getLong("person_settings_id"));

        return person;
    };


}
