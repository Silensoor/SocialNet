package socialnet.repository;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.api.request.UserUpdateDto;
import socialnet.exception.PostException;
import socialnet.model.Person;
import socialnet.utils.Reflection;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PersonRepository {
    private final JdbcTemplate jdbcTemplate;
    private final Reflection reflection;

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
        try {
            return jdbcTemplate.queryForObject(
                "SELECT * FROM persons WHERE email = ?",
                personRowMapper,
                email
            );
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public Person findById(Long personId) {
        try {
            List<Person> personList = jdbcTemplate.query("SELECT * FROM persons WHERE id = ?",
                    new Object[]{personId}, new BeanPropertyRowMapper<>(Person.class));
            if (personList.isEmpty()) throw new PostException("Person с id " + personId + " не существует");
            return personList.get(0);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<Person> findAll(Long limit) {
        try {
            return this.jdbcTemplate.query(
                    "SELECT * FROM persons LIMIT ?",
                    new Object[]{limit},
                    personRowMapper
            );
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<Person> findAll() {
        try {
            return this.jdbcTemplate.query("SELECT * FROM persons", personRowMapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<Person> findFriendsAll(List<Long> friendsId) {
        String sql = "SELECT * FROM persons WHERE";
        String friendsIdString = friendsIdStringMethod(friendsId, sql);
        try {
            return this.jdbcTemplate.query(friendsIdString, personRowMapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    private String friendsIdStringMethod(List<Long> friendsId, String sql) {
        StringBuilder friendsIdString = new StringBuilder(sql);
        for (int i = 0; i < friendsId.size(); i++) {
            if (i < friendsId.size() - 1) {
                friendsIdString.append(" id =").append(friendsId.get(i)).append(" OR");
            } else {
                friendsIdString.append(" id =").append(friendsId.get(i));
            }
        }
        return friendsIdString.toString();
    }

    public Person findPersonsEmail(String email) {
        try {
            return this.jdbcTemplate.queryForObject(
                    "SELECT * FROM persons WHERE email = ?",
                    new Object[]{email},
                    personRowMapper
            );
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<Person> findByCity(String city) {
        try {
            return this.jdbcTemplate.query(
                    "SELECT * FROM persons WHERE city = ?",
                    new Object[]{city},
                    personRowMapper
            );
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public void deleteUser(String email) {
        jdbcTemplate.update("Delete from Persons Where email = ?", email);
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

    public void setPhoto(String photoHttpLink, Long userId) {
        jdbcTemplate.update("Update Persons Set photo = ? Where id = ?", photoHttpLink, userId);
    }

    public Boolean setPassword(Long userId, String password) {
        int rowCount = jdbcTemplate.update("Update Persons Set password = ? Where id = ?", password, userId);
        return rowCount == 1;
    }

    public Boolean setEmail(Long userId, String email) {
        int rowCount = jdbcTemplate.update("Update Persons Set email = ? Where id = ?", email, userId);
        return rowCount == 1;
    }

    public Person getPersonByEmail(String email) {
        return jdbcTemplate.queryForObject("SELECT * FROM Persons WHERE email = ?",
                personRowMapper,
                email);
    }

    public Long getPersonIdByEmail(String email) {
        return jdbcTemplate.queryForObject("Select id from Persons where email = ?",
                new Object[] {email}, Long.class);
    }

    public void updatePersonInfo(UserUpdateDto userData, String email) {
        var sqlParam = reflection.getSqlWithoutNullable(userData, new Object[]{email});
        jdbcTemplate.update("Update Persons Set " + sqlParam.get("sql") + " where email = ?",
                (Object[]) sqlParam.get("values"));
    }


    public List<Person> findPersonsQuery(Integer age_from,
                                         Integer age_to, String city, String country,
                                         String first_name, String last_name,
                                         Integer offset, Integer perPage, Boolean flagQueryAll) {
        try {
            return jdbcTemplate.query(createSqlPerson(age_from, age_to, city, country, first_name, last_name,
                    flagQueryAll), personRowMapper, offset, perPage);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public Integer findPersonsQueryAll(Integer age_from, Integer age_to, String city, String country,
                                       String first_name, String last_name, Boolean flagQueryAll) {
        try {
            return jdbcTemplate.queryForObject(createSqlPerson(age_from, age_to, city,
                    country, first_name, last_name, flagQueryAll), Integer.class);
        } catch (EmptyResultDataAccessException ignored) {
            return 0;
        }
    }

    private String createSqlPerson(Integer age_from, Integer age_to, String city, String country,
                                   String first_name, String last_name, Boolean flagQueryAll) {
        StringBuilder str = new StringBuilder();
        String sql;
        if (flagQueryAll){
            str.append("SELECT COUNT(*) FROM persons WHERE is_deleted=false AND ");
        } else {
            str.append("SELECT * FROM persons WHERE is_deleted=false AND ");
        }
        val ageFrom = searchDate(age_from);
        val ageTo = searchDate(age_to);
        str.append(age_from > 0 ? " birth_date < '" + ageFrom + "' AND " : "");
        str.append(age_to > 0 ? " birth_date > '" + ageTo + "' AND " : "");
        str.append(!city.equals("") ? " city = '" + city + "' AND " : "");
        str.append(!country.equals("") ? " country = '" + country + "' AND " : "");
        str.append(!first_name.equals("") ? " first_name = '" + first_name + "' AND " : "");
        str.append(!last_name.equals("") ? " last_name = '" + last_name + "' AND " : "");
        if (str.substring(str.length() - 5).equals(" AND ")) {
            sql = str.substring(0, str.length() - 5);
        } else {
            sql = str.toString();
        }
        if (flagQueryAll) {
            return sql;
        } else {
            return sql + " OFFSET ? LIMIT ?";
        }
    }

    private Timestamp searchDate(Integer age) {
        val timestamp = new Timestamp(new Date().getTime());
        timestamp.setYear(timestamp.getYear() - age);
        return timestamp;
    }


    public Person findPersonsName(String author) {
        try {
            final String x1 = author.substring(0, author.indexOf(" ")).toLowerCase();
            final String x = author.substring(author.indexOf(" ") + 1).toLowerCase();
            return jdbcTemplate.queryForObject("SELECT * FROM persons" +
                            " WHERE is_deleted=false AND lower (first_name) = ? AND lower (last_name) = ?",
                    personRowMapper, x1, x);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public void updateOnlineStatus(Long personId, String status) {
        jdbcTemplate.update("UPDATE persons SET online_status = ? WHERE id = ?", status, personId);
    }

    public void updateLastOnlineTime(Long personId, Timestamp lastOnlineTime) {
        jdbcTemplate.update("UPDATE persons SET last_online_time = ? WHERE id = ?", lastOnlineTime, personId);
    }
}
