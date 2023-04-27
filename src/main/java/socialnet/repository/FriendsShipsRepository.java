package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Friendships;
import socialnet.model.enums.FriendshipStatusTypes;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendsShipsRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Friendships> friendshipsRowMapper = (resultSet, rowNum) -> {
        Friendships friendships = new Friendships();
        friendships.setId(resultSet.getLong("id"));
        friendships.setSentTime(resultSet.getTimestamp("sent_time"));
        friendships.setDstPersonId(resultSet.getLong("dst_person_id"));
        friendships.setSrcPersonId(resultSet.getLong("src_person_id"));
        friendships.setStatusName(FriendshipStatusTypes.valueOf(resultSet.getString("status_name")));
        return friendships;
    };

    public Friendships findFriend(Long id, Long idFriend) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM friendships" +
                            " WHERE status_name = 'FRIEND' AND (dst_person_id = ? AND src_person_id = ?)" +
                            " OR (dst_person_id = ? AND src_person_id = ?)",
                    friendshipsRowMapper, id, idFriend, idFriend, id);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public void insertStatusFriend(Long id, FriendshipStatusTypes status) {
        jdbcTemplate.update("UPDATE friendships SET status_name = ? WHERE id = ?",
                status.toString(), id);
    }

    public void addFriend(Long id, Long idFriend, FriendshipStatusTypes status) {
        jdbcTemplate.update("INSERT INTO friendships (sent_time, dst_person_id, src_person_id, status_name)" +
                " VALUES (NOW(), ?, ?, ?)", idFriend, id, status.toString());
    }

    public void updateFriend(Long id, Long idFriend, FriendshipStatusTypes status, Long idRequest) {
        jdbcTemplate.update("UPDATE friendships SET sent_time=NOW()," +
                " dst_person_id=?, src_person_id=?, status_name=? WHERE id=?", idFriend, id, status.toString(),
                idRequest);
    }

    public void deleteFriendUsing(Long id) {
        jdbcTemplate.update("DELETE FROM friendships WHERE id = ?", id);
    }

    public Friendships findRequest(Long id, Long idFriend) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM friendships" +
                            " WHERE status_name = 'REQUEST' AND (dst_person_id = ? AND src_person_id = ?)" +
                            " OR (dst_person_id = ? AND src_person_id = ?)",
                    friendshipsRowMapper, id, idFriend, idFriend, id);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }
}
