package socialnet.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import socialnet.model.Like;
import socialnet.dto.CommentRs;
import socialnet.dto.PersonRs;
import socialnet.repository.LikeRepository;
import socialnet.repository.PersonRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostCommentMapper implements RowMapper<CommentRs> {

    private final PersonRepository personRepository;
    private final LikeRepository likeRepository;
    @Override
    public CommentRs mapRow(ResultSet resultSet, int i) throws SQLException {
        long authorId = resultSet.getLong("author_id");
//        PersonRs author = personRepository.getPersonById(authorId);
        int id = (int) resultSet.getLong("id");
        List<Like> likesList = likeRepository.getLikesByEntityId(id);
        int likes = likesList.size();
        boolean myLike = containsMyLike(likesList);
        String commentText = resultSet.getString("comment_text");
        boolean isBlocked = resultSet.getBoolean("is_blocked");
        boolean isDeleted = resultSet.getBoolean("is_deleted");
        Timestamp timeStamp = resultSet.getTimestamp("time");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        String time = dateFormat.format(new Date(timeStamp.getTime()));
        int parentId = (int) resultSet.getLong("parent_id");
        int postId = (int) resultSet.getLong("post_id");
//        List<CommentRs> subComments = commentsRepository.getCommentsByEntityId(id);
//        return new CommentRs(author, id, isBlocked, isBlocked, likes, myLike, parentId, postId, null, time);
        return null;
    }

    private boolean containsMyLike(List<Like> likesList) {
        long myId = 0;
        List<Long> personIdList = likesList.stream().map(Like::getPersonId).collect(Collectors.toList());
        return personIdList.contains(myId);
    }
}
