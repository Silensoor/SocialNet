package socialnet.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import socialnet.model.db.Like;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class LikeMapper implements RowMapper<Like> {
    @Override
    public Like mapRow(ResultSet resultSet, int i) throws SQLException {
        long id = resultSet.getLong("id");
        String type = resultSet.getString("type");
        long entityId = resultSet.getLong("entity_id");
        Timestamp timestamp = resultSet.getTimestamp("time");
        long personId = resultSet.getLong("person_id");
        return new Like(id,type, entityId, timestamp, personId);
    }
}
