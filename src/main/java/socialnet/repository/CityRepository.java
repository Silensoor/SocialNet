package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.model.City;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CityRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<City> getCitiesByStarts(String country, String starts) {
        return jdbcTemplate.query("Select C1.* from Cities C1\n" +
                        "join Countries C2 on C1.country_id = C2.id\n" +
                        "Where C2.name = ?\n" +
                        "  and C1.name like ?",
                new Object[]{country, starts},
                new BeanPropertyRowMapper<>(City.class));
    }

    public List<City> getCitiesByCountry(String country) {
        return jdbcTemplate.query("Select C1.* from Cities C1\n" +
                        "join Countries C2 on C1.country_id = C2.id\n" +
                        "Where C2.name = ?",
                new Object[]{country},
                new BeanPropertyRowMapper<>(City.class));
    }
}
