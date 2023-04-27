package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.model.Weather;
import socialnet.utils.Reflection;

@Repository
@RequiredArgsConstructor
public class WeatherRepository {
    private final JdbcTemplate jdbcTemplate;
    private final Reflection reflection;

    public void saveWeather(Weather weather) {
        var sqlParam = reflection.getFieldNamesAndValues(weather, null);
        jdbcTemplate.update("Update or Insert into Wheather (" + sqlParam.get("fieldNames") + ")" +
                " values (" + sqlParam.get("values") + ")");
    }
}
