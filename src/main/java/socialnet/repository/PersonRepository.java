package socialnet.repository;

import lombok.RequiredArgsConstructor;
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
}
