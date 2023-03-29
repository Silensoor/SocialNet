package socialnet.repository.friends;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Friendships;
import socialnet.model.enums.FriendshipStatusTypes;

import java.sql.Timestamp;
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
                new Object[] { id, id }, friendshipsRowMapper);
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
                " WHERE (dst_person_id = ? AND src_person_id = ?) OR (dst_person_id = ? AND src_person_id = ?)",
                new Object[] { id, idFriend, idFriend, id }, friendshipsRowMapper);
    }

    public void insertStatusFriend(Long id, String status) {
        this.jdbcTemplate.update("UPDATE friendships SET status_name = ? WHERE id = ?", status, id);
    }

    public List<Friendships> findAllOutgoingRequests(Long id) {
        return this.jdbcTemplate.query("SELECT * FROM friendships" +
                        " WHERE status_name = 'REQUEST' AND src_person_id = ?",
                new Object[] { id }, friendshipsRowMapper);
    }

    public List<Friendships> findAllPotentialFriends(Long id) {
        return this.jdbcTemplate.query("SELECT * FROM friendships" +
                        " WHERE status_name = 'REQUEST' AND dst_person_id = ?",
                new Object[] { id }, friendshipsRowMapper);
    }
    public void addFriend(Long id, Long idFriend, String status) {
        this.jdbcTemplate.update("INSERT INTO friendships (sent_time, dst_person_id, src_person_id, status_name)" +
                        "VALUES (NOW(), ?, ?, ?)", idFriend, id, status);
    }


    public void deleteSentFriendshipRequest(Date date, String status, Long id) {
        this.jdbcTemplate.update("UPDATE friendships SET sent_time = ?," +
                " status = ? WHERE id = ? ", date, status, id);
        return;
    }

    public List<Friendships> sendFriendshipRequest(Long id) {
        return this.jdbcTemplate.query("SELECT * FROM friendships" +
                        " WHERE status_name = 'REQUEST' AND src_person_id = ?)",
                new Object[] { id }, friendshipsRowMapper);
    }

    public void sendFriendshipRequestUsingPOST(Date date, Long idfriend, long id, String status) {
        this.jdbcTemplate.query("INSERT INTO friendships (sent_time, dst_person_id, src_person_id, status_name)" +
                "VALUES (?, ?, ?, ?)", (RowCallbackHandler) date, idfriend, id, status);
    }

    public void deleteFriendUsing(Integer id) {
        this.jdbcTemplate.update("DELETE FROM friendships WHERE id = ?", id);
    }
}
