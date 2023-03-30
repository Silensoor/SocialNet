package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.exception.PostException;
import socialnet.exception.RegisterException;
import socialnet.mapper.PersonMapper;
import socialnet.dto.PersonRs;
import socialnet.model.Person;
import socialnet.model.Tag;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PersonRepository {
    private final JdbcTemplate jdbcTemplate;

    public Person findByEmail(long id) {
        String select = "SELECT * FROM persons WHERE id = ?";
        Object[] objects = new Object[]{id};
        List<Person> personList = jdbcTemplate.query(select, objects, new BeanPropertyRowMapper<>(Person.class));
        if(personList.isEmpty()) throw new PostException("Пользователя с id = " + id + " не существует");
        return personList.get(0);
    }
    public int save(Person person) {
        return jdbcTemplate.update(
                "insert into persons (email, first_name, last_name, password, reg_date, is_approved, is_blocked, is_deleted) values (?, ?, ?, ?, ?, ?, ?, ?)",
                person.getEmail(),
                person.getFirstName(),
                person.getLastName(),
                person.getPassword(),
                person.getRegDate(),
                true,
                false,
                false
        );
    }
    public Person findByEmail(String email) {
        String select = "SELECT * FROM persons WHERE email = \'" + email + "\'";
        List<Person> personList = jdbcTemplate.query(select, new BeanPropertyRowMapper<>(Person.class));
        if(personList.isEmpty()) return null;
        return personList.get(0);
    }
}
