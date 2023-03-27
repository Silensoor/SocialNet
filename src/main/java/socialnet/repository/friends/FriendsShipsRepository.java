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

    public List<Friendships> findAllFriendships(Long id) {
        return this.jdbcTemplate.query("SELECT * FROM friendships" +
                " WHERE status_name = 'FRIEND' AND (dst_person_id = ? OR src_person_id = ?)", friendshipsRowMapper);
    }

//    public Friendships findFriendships(Integer id) {
//        return (Friendships) this.jdbcTemplate.query("SELECT * FROM friendships WHERE id = ?", friendshipsRowMapper);
//    }

    private final RowMapper<Friendships> friendshipsRowMapper = (resultSet, rowNum) -> {
        Friendships friendships = new Friendships();
        friendships.setId(resultSet.getLong("id"));
        friendships.setSentTime(resultSet.getTimestamp("sent_time"));
        friendships.setDstPersonId(resultSet.getLong("dst_person_id"));
        friendships.setSrcPersonId(resultSet.getLong("src_person_id"));
        friendships.setStatusName(FriendshipStatusTypes.valueOf(resultSet.getString("status_name")));
        return friendships;
    };
}
