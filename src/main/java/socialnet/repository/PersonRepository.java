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
import java.util.Objects;



import java.util.Objects;




@Repository
@RequiredArgsConstructor
public class PersonRepository {
    private final JdbcTemplate jdbcTemplate;
    private final Reflection reflection;


    public Long insert(Person person) {
        String sql = "Insert into Persons " + reflection.getFieldNames(person, new String[] {"id"}) +
                " values " + reflection.getStringValues(person, "id") + ") " +
                 " returning Id";
        Object[] values = reflection.getValues(person, "id");
        jdbcTemplate.update(sql, values);


        return null;
    }
    public void save(Person person) {
        jdbcTemplate.update(
                "INSERT INTO persons " +
                "(email, first_name, last_name, password, reg_date, is_approved, is_blocked, is_deleted, telegram_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                person.getEmail().toLowerCase(),
                person.getFirstName(),
                person.getLastName(),
                person.getPassword(),
                person.getRegDate(),
                person.getIsApproved(),
                person.getIsBlocked(),
                person.getIsDeleted(),
                person.getTelegramId()
        );
    }

    public List<Person> findPersonsByBirthDate(){
        return jdbcTemplate.query("select * from persons as p  " +
                "where extract(month from timestamp 'now()')=extract(month from p.birth_date) " +
                "and extract(day from timestamp 'now()')=extract(day from p.birth_date)",personRowMapper);
    }

    public Person findByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT * FROM persons WHERE lower(email) = ?",
                personRowMapper,
                email.toLowerCase()
            );
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public Person findByTelegramId(long telegramId) {
        try {

            return jdbcTemplate.queryForObject(
                "SELECT * FROM persons WHERE telegram_id = ?",
                personRowMapper,
                telegramId
            );


            List<Person> personList = jdbcTemplate.query("SELECT * FROM persons WHERE id = ?",
                    new Object[]{personId}, new BeanPropertyRowMapper<>(Person.class));
            if (personList.isEmpty()) throw new PostException("Person с id " + personId + " не существует");

            List<Person> personList = jdbcTemplate.query("SELECT * FROM persons WHERE is_deleted=false AND id = ?",
                    new Object[]{authorId}, new BeanPropertyRowMapper<>(Person.class));
            if (personList.isEmpty()) throw new PostException("Person с id " + authorId + " не существует");

            return personList.get(0);

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
            return jdbcTemplate.query("SELECT * FROM persons", personRowMapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }


    public List<Person> findFriendsAll(Long id, Integer offset, Integer perPage) {
        try {
            return jdbcTemplate.query("SELECT DISTINCT p.id, p.about, p.birth_date," +
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

    public List<Person> findFriendsAll(List<Long> friendsId) {
        try {
            return this.jdbcTemplate.query(friendsIdStringMethod(friendsId), personRowMapper);

        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }


    public List<Person> findAllOutgoingRequests(Long id, Integer offset, Integer perPage) {
        try {
            return jdbcTemplate.query("SELECT DISTINCT p.id, p.about, p.birth_date," +
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

    private String friendsIdStringMethod(List<Long> friendsId) {
        StringBuilder friendsIdString = new StringBuilder("SELECT * FROM persons WHERE is_deleted=false AND id IN (");
        for (int i = 0; i < friendsId.size(); i++)
            if (i < friendsId.size() - 1) {
                friendsIdString.append(friendsId.get(i)).append(", ");
            } else {
                friendsIdString.append(friendsId.get(i)).append(")");
            }
        return friendsIdString.toString();

    }

    public Integer findAllOutgoingRequestsAll(Long id) {
        try {

            return jdbcTemplate.queryForObject("SELECT DISTINCT COUNT(p.id) FROM persons AS p JOIN" +
                            " friendships ON friendships.dst_person_id=p.id OR friendships.src_person_id=p.id" +
                            " WHERE is_deleted = false AND status_name = 'REQUEST' AND src_person_id = ? AND NOT p.id=?",
                    Integer.class, id, id);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public Integer findFriendsAllCount(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT DISTINCT COUNT(persons.id) FROM persons JOIN" +
                            " friendships ON friendships.dst_person_id=persons.id" +
                            " OR friendships.src_person_id=persons.id WHERE is_deleted = false AND" +
                            " (friendships.dst_person_id=? OR friendships.src_person_id=?)" +
                            " AND friendships.status_name='FRIEND'  AND NOT persons.id=?",
                    Integer.class, id, id, id);


            return this.jdbcTemplate.queryForObject(
                    "SELECT * FROM persons WHERE email = ?",
                    new Object[]{email},
                    personRowMapper
            );

            return this.jdbcTemplate.queryForObject("SELECT * FROM persons WHERE email = ?",
                    new Object[]{email}, personRowMapper);


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

                "SELECT * FROM persons WHERE is_deleted=false AND city = ?",
                new Object[] { city },
                personRowMapper

            );
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public void markUserDelete(String email) {
        jdbcTemplate.update("Update Persons Set is_deleted = true Where email = ?", email);
    }

    public void recover(String email) {
        jdbcTemplate.update("Update Persons Set is_deleted = false Where email = ?", email);
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
                                         Integer offset, Integer perPage, Boolean flagQueryAll, Integer id) {
        try {
            return jdbcTemplate.query(createSqlPerson(age_from, age_to, city, country, first_name, last_name,
                    flagQueryAll, id), personRowMapper, offset, perPage);



    public List<Person> findPersonsQuery(Object[] args) {
        String sql = createSqlPerson(args);

    public List<Person> findPersonsQuery(Integer age_from,
                                         Integer age_to, String city, String country,
                                         String first_name, String last_name,
                                         Integer offset, Integer perPage) {

        try {

            return this.jdbcTemplate.query(createSqlPerson(age_from, age_to, city, country, first_name, last_name,
                    offset, perPage), personRowMapper);


            return null;
    } catch (EmptyResultDataAccessException ignored) {
            return null;


        } catch (EmptyResultDataAccessException ignored) {
            return null;

        }
    }


    public Integer findPersonsQueryAll(Integer age_from, Integer age_to, String city, String country,
                                       String first_name, String last_name, Boolean flagQueryAll, Integer id) {
        try {
            return jdbcTemplate.queryForObject(createSqlPerson(age_from, age_to, city,
                    country, first_name, last_name, flagQueryAll, id), Integer.class);
        } catch (EmptyResultDataAccessException ignored) {
            return 0;
        }
    }

    private String createSqlPerson(Integer age_from, Integer age_to, String city, String country,
                                   String first_name, String last_name, Boolean flagQueryAll, Integer id) {
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
                .append(!country.equals("") ? " country = '" + country + "' AND " : "");
        if (first_name.equals("'")){
            first_name = "\"";
        }
        str.append(!first_name.equals("") ? " first_name = '" + first_name + "' AND " : "")
                .append(!last_name.equals("") ? " last_name = '" + last_name + "' AND " : "");
        str.append(" NOT id = " + id + " ");
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

    private String createSqlPerson(Integer age_from, Integer age_to, String city, String country,
                                   String first_name, String last_name, Integer offset, Integer perPage) {
        StringBuilder str = new StringBuilder();
        String sql;
        str.append("SELECT * FROM persons WHERE is_deleted=false AND ");
        val ageFrom = searchDate(age_from);
        val ageTo = searchDate(age_to);
        str.append(age_from > 0 ? " birth_date < '" + ageFrom + "' AND " : "");
        str.append(age_to > 0 ? " birth_date > '" + ageTo + "' AND " : "");
        str.append(city.equals("") ? " city = '" + city + "' AND " : "");
        str.append(country.equals("") ? " country = '" + country + "' AND " : "");
        str.append(first_name.equals("") ? " first_name = '" + first_name + "' AND " : "");
        str.append(last_name.equals("") ? " last_name = '" + last_name + "' AND " : "");
        if (str.substring(str.length() - 5).equals(" AND ")) {
            sql = str.substring(0, str.length() - 5);
        } else {
            sql = str.toString();
        }
        return sql + " OFFSET " + offset + " LIMIT " + perPage;

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

    public Long findPersonsName(String author) {
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

            return Objects.requireNonNull(this.jdbcTemplate.query("SELECT * FROM persons" +
                            " WHERE is_deleted=false AND first_name = ? AND last_name = ?",
                    new Object[]{author.substring(0, author.indexOf(" ")),
                            author.substring(author.indexOf(" "))},
                    new BeanPropertyRowMapper<>(Person.class)).stream().findAny().orElse(null)).getId();
        } catch (EmptyResultDataAccessException ignored) {

    public Long findPersonsName(String nameFirst, String nameLast) {



            return null;


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
            return jdbcTemplate.query(sql.toString(), personRowMapper);
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
            return jdbcTemplate.query("SELECT DISTINCT p.id, p.about, p.birth_date, p.change_password_token," +
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

    public Long countAllPotentialFriends(Long id) {
        return jdbcTemplate.queryForObject(
            "SELECT DISTINCT COUNT(p.*) " +
            "  FROM persons AS p " +
            "  JOIN friendships ON friendships.dst_person_id=p.id OR friendships.src_person_id=p.id " +
            " WHERE is_deleted = false AND friendships.dst_person_id=? AND NOT p.id=? " +
            "   AND friendships.status_name = 'REQUEST' ",
            Long.class, id, id);
    }

    public List<Person> findByCityForFriends(Long id, String city, String friendsRecommended,
                                             Integer offset, Integer perPage) {
        StringBuilder sql = new StringBuilder("SELECT * FROM persons WHERE is_deleted = false AND city = ?")
                .append(!Objects.equals(friendsRecommended, "") ? " AND NOT id IN(" + friendsRecommended + ")" : "")
                .append(" AND NOT id=? ORDER BY reg_date DESC OFFSET ? LIMIT ?");
        try {
            return jdbcTemplate.query(sql.toString(), personRowMapper, city, id, offset, perPage);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<Person> findAllForFriends(Long id, String friendsRecommended, Integer perPage) {
        StringBuilder sql = new StringBuilder("SELECT * FROM persons WHERE is_deleted = false AND NOT id IN(")
                .append(friendsRecommended)
                .append(") AND NOT id=? ORDER BY reg_date DESC LIMIT ?");
        try {
            return jdbcTemplate.query(sql.toString(), personRowMapper, id, perPage);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public Integer getAllUsersByCountry(String country) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM persons WHERE country=?", Integer.class, country);
    }
}
