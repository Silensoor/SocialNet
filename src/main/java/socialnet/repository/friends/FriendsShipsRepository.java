package socialnet.repository.friends;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Friendships;
import socialnet.model.enums.FriendshipStatusTypes;

import java.util.Date;
import java.util.List;

@Repository
public class FriendsShipsRepository {

    private JdbcTemplate jdbcTemplate;


    public FriendsShipsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Friendships> findAllFriendships(Long id) {
        return this.jdbcTemplate.query("SELECT * FROM friendships" +
                " WHERE status_name = 'FRIEND' AND (dst_person_id = ? OR src_person_id = ?)",
                new Object[] { id }, friendshipsRowMapper);
    }

    private final RowMapper<Friendships> friendshipsRowMapper = (resultSet, rowNum) -> {
        Friendships friendships = new Friendships();
        friendships.setId(resultSet.getLong("id"));
        friendships.setSentTime(resultSet.getTimestamp("sent_time"));
        friendships.setDstPersonId(resultSet.getLong("dst_person_id"));
        friendships.setSrcPersonId(resultSet.getLong("src_person_id"));
        friendships.setStatusName(FriendshipStatusTypes.valueOf(resultSet.getString("status_name")));
        return friendships;
    };

    public List<Friendships> findFriend(Long id, Integer idFriend) {
        return this.jdbcTemplate.query("SELECT * FROM friendships" +
                " WHERE dst_person_id = ?1 OR src_person_id = ?2)",
                new Object[] { id, idFriend }, friendshipsRowMapper);
    }

    public void insertStatusFriend(Long id, String status) {
        this.jdbcTemplate.update("UPDATE friendships SET status_name = ? WHERE id = ? ", status, id);
        return;
    }

    public List<Friendships> findAllOutgoingRequests(Long id) {
        return this.jdbcTemplate.query("SELECT * FROM friendships" +
                        " WHERE status_name = 'REQUEST' AND src_person_id = ?)",
                new Object[] { id }, friendshipsRowMapper);
    }

    public void addFriend(Date time, Long id, Long idFriend, String status) {
        this.jdbcTemplate.query("INSERT INTO friendships (sent_time, dst_person_id, src_person_id, status_name)" +
                        "VALUES (?, ?, ?, ?)", (RowCallbackHandler) time, idFriend, id, status);
    }


}
