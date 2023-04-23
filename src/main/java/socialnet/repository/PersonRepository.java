package socialnet.repository;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
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

    public Person findById(Long authorId) {
        try {
            List<Person> personList = jdbcTemplate.query("SELECT * FROM persons WHERE id = ?",
                    new Object[]{authorId}, new BeanPropertyRowMapper<>(Person.class));
            if (personList.isEmpty()) throw new PostException("Person с id " + authorId + " не существует");
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
        int rowCount = jdbcTemplate.update("Update Persons Set photo = ? Where id = ?", photoHttpLink, userId);
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

    public void updatePersonInfo(UserUpdateDto userData, String email) {
        var sqlParam = reflection.getSqlWithoutNullable(userData, new Object[] {email});
        String sql = "Update Persons Set " + sqlParam.get("sql") + " where email = ?";
        Object[] values = (Object[]) sqlParam.get("values");

        jdbcTemplate.update(sql, values);
    }


    public List<Person> findPersonsQuery(Object[] args) {
        String sql = createSqlPerson(args);
        try {
            //DSLContext dsl = DSL.using((Connection) jdbcTemplate.getDataSource());
//            return  dsl.select()
//                    .from(table("persons"))
//                    .where(field("is_deleted").eq(false)
//                            .and(field("is_blocked").eq(false)
//                                    .and(sql(sql))))
//                    .limit((Integer) args[8])
//                    .offset((Integer) args[7])
//                    .fetchInto(Person.class);
            return null;
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    private String createSqlPerson(Object[] args) {
        String sql = " ";
        if ((Integer) args[1] > 0) {
            val ageFrom = searchDate((Integer) args[1]);
            sql = sql + " birth_date < '" + ageFrom + "' AND ";
        }
        if ((Integer) args[2] > 0) {
            val ageTo = searchDate((Integer) args[2]);
            sql = sql + " birth_date > '" + ageTo + "' AND ";
        }
        if (!args[3].equals("")) {
            sql = sql + " city = '" + args[3] + "' AND ";
        }
        if (!args[4].equals("")) {
            sql = sql + " country = '" + args[4] + "' AND ";
        }
        if (!args[5].equals("")) {
            sql = sql + " first_name = '" + args[5] + "' AND ";
        }
        if (!args[6].equals("")) {
            sql = sql + " last_name = '" + args[6] + "' AND ";
        }
        String str = sql.substring(sql.length() - 5);
        if (str.equals(" AND ")) {
            return sql.substring(0, sql.length() - 5);
        }
        return sql;
    }

    private Timestamp searchDate(Integer age) {
        val timestamp = new Timestamp(new Date().getTime());
        timestamp.setYear(timestamp.getYear() - age);
        return timestamp;
    }


    public Long findPersonsName(String nameFirst, String nameLast) {
        try {
//            DSLContext dsl = DSL.using((Connection) jdbcTemplate.getDataSource());
//            final Result<Record> person = dsl.select()
//                    .from(table("persons"))
//                    .where(field("first_name").eq(nameFirst)
//                            .and(field("last_name").eq(nameLast)))
//                    .fetch();
//            return  (Long) person.get(1).get("id");
            return null;
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }
}
