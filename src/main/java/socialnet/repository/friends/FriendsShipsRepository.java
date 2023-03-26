package socialnet.repository.friends;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Friendships;
import socialnet.model.enums.FriendshipStatusTypes;

import java.util.List;

@Repository
public class FriendsShipsRepository {

    private JdbcTemplate jdbcTemplate;


    public FriendsShipsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Friendships> findAllFriendships() {
        return this.jdbcTemplate.query("SELECT * FROM friendships", friendshipsRowMapper);
    }

    public Friendships findFriendships(Integer id) {
        return (Friendships) this.jdbcTemplate.query("SELECT * FROM friendships WHERE id = ?", friendshipsRowMapper);
    }

    private final RowMapper<Friendships> friendshipsRowMapper = (resultSet, rowNum) -> {
        Friendships friendships = new Friendships();
        friendships.setId(resultSet.getLong("id"));
        friendships.setSentTime(resultSet.getTimestamp("sentTime"));
        friendships.setDstPersonId(resultSet.getLong("dstPersonId"));
        friendships.setSrcPersonId(resultSet.getLong("srcPersonId"));
        friendships.setStatusName(FriendshipStatusTypes.valueOf(resultSet.getString("statusName")));
        return friendships;
    };
}
