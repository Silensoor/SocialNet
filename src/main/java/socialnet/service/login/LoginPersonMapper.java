package socialnet.service.login;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPersonMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(ResultSet resultSet, int i) throws SQLException {

        Person person = new Person();

        person.setEmail(resultSet.getString("e_mail"));
        person.setPassword(resultSet.getString("password"));

        return person;
    }
}
