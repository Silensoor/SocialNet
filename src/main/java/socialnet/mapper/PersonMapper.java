package socialnet.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import socialnet.model.rs.PersonRs;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PersonMapper implements RowMapper<PersonRs> {

    @Override
    public PersonRs mapRow(ResultSet resultSet, int i) throws SQLException {
        String about = resultSet.getString("about");
        String birthDate = resultSet.getTimestamp("birth_date");

    }
}
