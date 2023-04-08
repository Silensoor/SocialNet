package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.model.Country;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CountryRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Country> findAll() {
        return jdbcTemplate.query("Select * from Countries order by name",
                new Object[]{},
                new BeanPropertyRowMapper<>(Country.class));
    }

}
