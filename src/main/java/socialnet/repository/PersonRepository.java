package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.mapper.PersonMapper;
import socialnet.dto.PersonRs;
import socialnet.model.Person;
import socialnet.model.Tag;

@Repository
@RequiredArgsConstructor
public class PersonRepository {
    private final JdbcTemplate jdbcTemplate;

    public Person getPersonById(long id) {
        System.out.println(id);
        String select = "SELECT * FROM persons WHERE id = ?";
        Object[] objects = new Object[]{id};
        return jdbcTemplate.query(select, objects, new BeanPropertyRowMapper<>(Person.class)).get(0);
    }
    public int save(Person person) {
        return jdbcTemplate.update(
                "insert into persons (email, first_name, last_name, password, reg_date) values (?, ?, ?, ?, ?)",
                person.getEmail(),
                person.getFirstName(),
                person.getLastName(),
                person.getPassword(),
                person.getRegDate()
        );
    }
    public Person findByEmail(String email) {
        String select = "SELECT * FROM persons WHERE email = " + email;
        return jdbcTemplate.query(select, new BeanPropertyRowMapper<>(Person.class)).get(0);
    }
}
