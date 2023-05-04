package socialnet.repository;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.api.request.UserUpdateDto;
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
            return jdbcTemplate.queryForObject("SELECT * FROM persons WHERE id = ?",
                    new BeanPropertyRowMapper<>(Person.class), personId);
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

    public List<Person> findFriendsAll(Long id, Integer offset, Integer perPage) {
        try {
            return this.jdbcTemplate.query("SELECT DISTINCT p.id, p.about, p.birth_date," +
                            " p.change_password_token, p.configuration_code, p.deleted_time," +
                            " p.email, p.first_name, p.is_approved, p.is_blocked, p.is_deleted," +
                            " p.last_name, p.last_online_time, p.message_permissions," +
                            " p.notifications_session_id, p.online_status, p.password, p.phone," +
                            " p.photo, p.reg_date, p.city, p.country, p.telegram_id," +
                            " p.person_settings_id FROM persons AS p JOIN friendships ON" +
                            " friendships.dst_person_id=p.id OR friendships.src_person_id=p.id WHERE is_deleted = false" +
                            " AND (friendships.dst_person_id=? OR friendships.src_person_id=?) AND" +
                            " friendships.status_name='FRIEND' AND NOT p.id=? ORDER BY p.last_online_time DESC" +
                            " OFFSET ? LIMIT ?",
                    personRowMapper, id, id, id, offset, perPage);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<Person> findAllOutgoingRequests(Long id, Integer offset, Integer perPage) {
        try {
            return this.jdbcTemplate.query("SELECT DISTINCT p.id, p.about, p.birth_date," +
                            " p.change_password_token, p.configuration_code, p.deleted_time," +
                            " p.email, p.first_name, p.is_approved, p.is_blocked," +
                            " p.is_deleted, p.last_name, p.last_online_time," +
                            " p.message_permissions, p.notifications_session_id, p.online_status," +
                            " p.password, p.phone, p.photo, p.reg_date, p.city," +
                            " p.country, p.telegram_id, p.person_settings_id FROM persons AS p" +
                            " JOIN friendships ON friendships.src_person_id=p.id  OR friendships.dst_person_id=p.id" +
                            " WHERE is_deleted = false AND friendships.src_person_id=? AND NOT p.id=?" +
                            " AND friendships.status_name = 'REQUEST' OFFSET ? LIMIT ?",
                    personRowMapper, id, id, offset, perPage);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public Integer findAllOutgoingRequestsAll(Long id) {
        try {
            return this.jdbcTemplate.queryForObject("SELECT DISTINCT COUNT(p.id) FROM persons AS p JOIN" +
                            " friendships ON friendships.dst_person_id=p.id OR friendships.src_person_id=p.id" +
                            " WHERE is_deleted = false AND status_name = 'REQUEST' AND src_person_id = ? AND NOT p.id=?",
                    Integer.class, id, id);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public Integer findFriendsAllCount(Long id) {
        try {
            return this.jdbcTemplate.queryForObject("SELECT DISTINCT COUNT(persons.id) FROM persons JOIN" +
                            " friendships ON friendships.dst_person_id=persons.id" +
                            " OR friendships.src_person_id=persons.id WHERE is_deleted = false AND" +
                            " (friendships.dst_person_id=? OR friendships.src_person_id=?)" +
                            " AND friendships.status_name='FRIEND'  AND NOT persons.id=?",
                    Integer.class, id, id, id);
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

    public void setEmail(String oldEmail, String newEmail) {
        jdbcTemplate.update("Update Persons Set email = ? Where email = ?", newEmail, oldEmail);
    }

    public void setPassword(String newPassword, String email) {
        jdbcTemplate.update("Update Persons Set password = ? Where email = ?", newPassword, email);
    }

    public Person getPersonByEmail(String email) {
        return jdbcTemplate.queryForObject("SELECT * FROM Persons WHERE email = ?",
                personRowMapper,
                email);
    }

    public Long getPersonIdByEmail(String email) {
        return jdbcTemplate.queryForObject("Select id from Persons where email = ?",
                new Object[]{email}, Long.class);
    }

    public void updatePersonInfo(UserUpdateDto userData, String email) {
        var sqlParam = reflection.getFieldsAndValuesQuery(userData, new Object[]{email});
        jdbcTemplate.update("Update Persons Set " + sqlParam.get("fieldNames") + " where email = ?",
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
        if (flagQueryAll) {
            str.append("SELECT COUNT(*) FROM persons WHERE is_deleted=false AND ");
        } else {
            str.append("SELECT * FROM persons WHERE is_deleted=false AND ");
        }
        val ageFrom = searchDate(age_from);
        val ageTo = searchDate(age_to);
        str.append(age_from > 0 ? " birth_date < '" + ageFrom + "' AND " : "")
                .append(age_to > 0 ? " birth_date > '" + ageTo + "' AND " : "")
                .append(!city.equals("") ? " city = '" + city + "' AND " : "")
                .append(!country.equals("") ? " country = '" + country + "' AND " : "")
                .append(!first_name.equals("") ? " first_name = '" + first_name + "' AND " : "")
                .append(!last_name.equals("") ? " last_name = '" + last_name + "' AND " : "");
        if (str.substring(str.length() - 5).equals(" AND ")) {
            sql = str.substring(0, str.length() - 5);
        } else {
            sql = str.toString();
        }
        if (!flagQueryAll) {
            return sql + " OFFSET ? LIMIT ?";
        } else {
            return sql;
        }
    }

    private Timestamp searchDate(Integer age) {
        val timestamp = new Timestamp(new Date().getTime());
        timestamp.setYear(timestamp.getYear() - age);
        return timestamp;
    }


    public Person findPersonsName(String author) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM persons" +
                            " WHERE is_deleted=false AND lower (first_name) = ? AND lower (last_name) = ?",
                    personRowMapper, author.substring(0, author.indexOf(" ")).toLowerCase(),
                    author.substring(author.indexOf(" ") + 1).toLowerCase());
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

    public List<Person> findRecommendedFriends(Long id, List<Person> friends, Integer offset, Integer perPage) {
        StringBuilder sql = new StringBuilder("SELECT DISTINCT p.id, p.about, p.birth_date, p.change_password_token," +
                " p.configuration_code, p.deleted_time, p.email, p.first_name, p.is_approved," +
                " p.is_blocked, p.is_deleted, p.last_name, p.last_online_time, p.message_permissions," +
                " p.notifications_session_id, p.online_status, p.password, p.phone, p.photo," +
                " p.reg_date, p.city, p.country, p.telegram_id, p.person_settings_id" +
                " FROM persons AS p JOIN friendships ON friendships.dst_person_id=p.id" +
                " OR friendships.src_person_id=p.id WHERE is_deleted = false AND ")
                .append(createSqlWhere(id, friends, offset, perPage));
        try {
            return this.jdbcTemplate.query(sql.toString(), personRowMapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public String createSqlWhere(Long id, List<Person> friends, Integer offset, Integer perPage) {
        StringBuilder strWhere = new StringBuilder();
        StringBuilder str = new StringBuilder();
        friends.forEach(friend -> str.append(friend.getId()).append(", "));
        if (!friends.isEmpty()) {
            strWhere.append(" (friendships.dst_person_id IN (")
                    .append(str.substring(0, str.length() - 2))
                    .append(") OR friendships.src_person_id IN (")
                    .append(str.substring(0, str.length() - 2))
                    .append("))")
                    .append(" AND NOT p.id IN (")
                    .append(str.substring(0, str.length() - 2))
                    .append(")")
                    .append(" AND NOT p.id=")
                    .append(id);
        } else {
            strWhere.append(" NOT p.id=").append(id);
        }
        strWhere.append(" OFFSET ").append(offset).append(" LIMIT ").append(perPage);
        return strWhere.toString();
    }

    public List<Person> findAllPotentialFriends(Long id, Integer offset, Integer perPage) {
        try {
            return this.jdbcTemplate.query("SELECT DISTINCT p.id, p.about, p.birth_date, p.change_password_token," +
                            " p.configuration_code, p.deleted_time, p.email, p.first_name, p.is_approved, p.is_blocked," +
                            " p.is_deleted, p.last_name, p.last_online_time, p.message_permissions," +
                            " p.notifications_session_id, p.online_status, p.password, p.phone, p.photo, p.reg_date," +
                            " p.city, p.country, p.telegram_id, p.person_settings_id FROM persons AS p" +
                            " JOIN friendships ON friendships.dst_person_id=p.id OR friendships.src_person_id=p.id" +
                            " WHERE is_deleted = false AND friendships.dst_person_id=? AND NOT p.id=?" +
                            " AND friendships.status_name = 'REQUEST' OFFSET ? LIMIT ?",
                    personRowMapper, id, id, offset, perPage);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<Person> findByCityForFriends(Long id, String city, String friendsRecommended,
                                             Integer offset, Integer perPage) {
        StringBuilder sql = new StringBuilder("SELECT * FROM persons WHERE is_deleted = false AND city = ?" +
                " AND NOT id IN(")
                .append(friendsRecommended)
                .append(") AND NOT id=? ORDER BY reg_date DESC OFFSET ? LIMIT ?");
        try {
            return this.jdbcTemplate.query(sql.toString(), personRowMapper, city, id, offset, perPage);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<Person> findAllForFriends(Long id, String friendsRecommended, Integer perPage) {
        StringBuilder sql = new StringBuilder("SELECT * FROM persons WHERE is_deleted = false AND NOT id IN(")
                .append(friendsRecommended)
                .append(") AND NOT id=? LIMIT ?");
        try {
            return this.jdbcTemplate.query(sql.toString(), personRowMapper, id, perPage);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }
}
