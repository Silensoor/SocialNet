package socialnet.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import socialnet.model.Tag;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TagMapper implements RowMapper<Tag> {
    @Override
    public Tag mapRow(ResultSet resultSet, int i) throws SQLException {
        String tag = resultSet.getString("teg");
        long id = resultSet.getLong("id");
        return new Tag(id, tag);
    }
}
