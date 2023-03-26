package socialnet.repository.friends;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Person;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class PersonRepository {

    private JdbcTemplate jdbcTemplate;

    public PersonRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Person> findAllPersons() {
        return this.jdbcTemplate.query("SELECT * FROM persons", personRowMapper);
    }

    public Person findPersonsId(Integer id) {
        return (Person) this.jdbcTemplate.query("SELECT * FROM persons WHERE id = ?", personRowMapper);
    }

    private final RowMapper<Person> personRowMapper = (resultSet, rowNum) -> {
        Person person = new Person();
        person.setId(resultSet.getInt("id"));
        person.setAbout(resultSet.getString("about"));
        person.setBirthDate(resultSet.getTimestamp("birthDate"));
        person.setChangePasswordToken(resultSet.getString("changePasswordToken"));
        person.setConfigurationCode(resultSet.getInt("configurationCode"));
        person.setDeletedTime(resultSet.getTimestamp("deletedTime"));
        person.setEmail(resultSet.getString("email"));
        person.setFirstName(resultSet.getString("firstName"));
        person.setApproved(resultSet.getBoolean("isApproved"));
        person.setBlocked(resultSet.getBoolean("isBlocked"));
        person.setDeleted(resultSet.getBoolean("isDeleted"));
        person.setLastName(resultSet.getString("lastName"));
        person.setLastOnlineTime(resultSet.getTimestamp("lastOnlineTime"));
        person.setMessagePermissions(resultSet.getString("messagePermissions"));
        person.setNotificationsSessionId(resultSet.getString("notificationsSessionId"));
        person.setOnlineStatus(resultSet.getString("onlineStatus"));
        person.setPassword(resultSet.getString("password"));
        person.setPhone(resultSet.getString("phone"));
        person.setPhoto(resultSet.getString("photo"));
        person.setRegDate(resultSet.getTimestamp("regDate"));
        person.setCity(resultSet.getString("city"));
        person.setCountry(resultSet.getString("country"));
        person.setTelegramId(resultSet.getLong("telegramId"));
        person.setPersonSettingsId(resultSet.getLong("personSettingsId"));
        return person;
    };
}
