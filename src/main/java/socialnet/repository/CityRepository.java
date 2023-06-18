package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.api.response.RegionStatisticsRs;
import socialnet.model.City;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CityRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<City> getCitiesByStarts(String country, String starts) {
        if (starts.isEmpty())
            starts = "-";
        String sql = String.format("Select C1.* from Cities C1\n" +
                "join Countries C2 on C1.country_id = C2.id\n" +
                "Where C2.name = '%s'\n" +
                "  and Lower(C1.name) like Lower('%s')\n" +
                "Order by Name", country, starts + "%");
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(City.class));
    }

    public List<City> getCitiesByCountry(String country) {
        return jdbcTemplate.query("Select C1.* from Cities C1\n" +
                        "join Countries C2 on C1.country_id = C2.id\n" +
                        "Where C2.name = ?\n" +
                        "Order by Name",
                new BeanPropertyRowMapper<>(City.class), country);
    }

    public Boolean containsCity(String city) {
        var rowCount = jdbcTemplate.query("Select Lower(name) from cities where (Lower(name) = Lower(?)) and Code2 = 'RU'",
                new BeanPropertyRowMapper<>(City.class), city);
        return rowCount.size() > 0;
    }

    public Integer getAllCity() {
        try {
            return jdbcTemplate.queryForObject("SELECT DISTINCT COUNT(cities.id) FROM cities", Integer.class);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<RegionStatisticsRs> getCitiesUsers() {
        try {
            return jdbcTemplate.query("SELECT DISTINCT cities.id, cities.name, (SELECT COUNT(*) FROM persons" +
                    " WHERE cities.name=persons.city) FROM cities", regionStatisticsRsRowMapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    private final RowMapper<RegionStatisticsRs> regionStatisticsRsRowMapper = (resultSet, rowNum) -> {
        RegionStatisticsRs rs = new RegionStatisticsRs();
        rs.setRegion(resultSet.getString(2));
        rs.setCountUsers(resultSet.getInt(3));
        return rs;
    };

    public City getCity(String city) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM cities WHERE cities.name=?",
                    new BeanPropertyRowMapper<>(City.class), city);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }
}
