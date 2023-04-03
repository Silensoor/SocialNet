package socialnet.repository;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Friendships;
import socialnet.model.enums.FriendshipStatusTypes;

import java.util.*;

@Repository
@AllArgsConstructor
public class FriendsShipsRepository {

    private JdbcTemplate jdbcTemplate;


    public List<Friendships> findAllFriendships(Long id) {
        try {
            return this.jdbcTemplate.query("SELECT * FROM friendships" +
                            " WHERE status_name = 'FRIEND' AND (dst_person_id = ? OR src_person_id = ?)",
                    new Object[]{id, id}, friendshipsRowMapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
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

    public List<Friendships> findFriend(Long id, Long idFriend) {
        try {
            return this.jdbcTemplate.query("SELECT * FROM friendships" +
                            " WHERE status_name = 'FRIEND' AND (dst_person_id = ? AND src_person_id = ?)" +
                            " OR (dst_person_id = ? AND src_person_id = ?)",
                    new Object[]{id, idFriend, idFriend, id}, friendshipsRowMapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public void insertStatusFriend(Long id, String status) {
        this.jdbcTemplate.update("UPDATE friendships SET status_name = ? WHERE id = ?", status, id);
    }

    public List<Friendships> findAllOutgoingRequests(Long id) {
        try {
            return this.jdbcTemplate.query("SELECT * FROM friendships" +
                            " WHERE status_name = 'REQUEST' AND src_person_id = ?",
                    new Object[]{id}, friendshipsRowMapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<Friendships> findAllPotentialFriends(Long id) {
        try {
            return this.jdbcTemplate.query("SELECT * FROM friendships" +
                            " WHERE status_name = 'REQUEST' AND dst_person_id = ?",
                    new Object[]{id}, friendshipsRowMapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public void addFriend(Long id, Long idFriend, String status) {
        this.jdbcTemplate.update("INSERT INTO friendships (sent_time, dst_person_id, src_person_id, status_name)" +
                "VALUES (NOW(), ?, ?, ?)", idFriend, id, status);
    }

    public void updateFriend(Long id, Long idFriend, String status, Long idRequest) {
        this.jdbcTemplate.update("UPDATE friendships SET sent_time=NOW()," +
                " dst_person_id=?, src_person_id=?, status_name=? WHERE id=?", idFriend, id, status, idRequest);
    }

    public void deleteSentFriendshipRequest(String status, Long id) {
        this.jdbcTemplate.update("UPDATE friendships SET sent_time = NOW()," +
                " status_name = ? WHERE id = ? ", status, id);
    }

    public List<Friendships> sendFriendshipRequest(Long id) {
        try {
            return this.jdbcTemplate.query("SELECT * FROM friendships" +
                            " WHERE status_name = 'REQUEST' AND src_person_id = ?",
                    new Object[]{id}, friendshipsRowMapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public void sendFriendshipRequestUsingPOST(Long idFriend, Long id, String status) {
        this.jdbcTemplate.update("INSERT INTO friendships (sent_time, dst_person_id, src_person_id, status_name) " +
                "VALUES (NOW(), ?, ?, ?)", idFriend, id, status);
    }

    public void deleteFriendUsing(Long id) {
        this.jdbcTemplate.update("DELETE FROM friendships WHERE id = ?", id);
    }
}
