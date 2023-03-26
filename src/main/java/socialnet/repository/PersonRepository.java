package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.mapper.PersonMapper;
import socialnet.dto.rs.PersonRs;

@Repository
@RequiredArgsConstructor
public class PersonRepository {
    private final JdbcTemplate jdbcTemplate;
    private final PersonMapper personMapper;

    public PersonRs getPersonById(long id) {
        jdbcTemplate.query("SELECT * FROM persons WHERE id = " + id, personMapper);
        return null;
    }
}
