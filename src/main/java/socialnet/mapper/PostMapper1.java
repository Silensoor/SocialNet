package socialnet.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import socialnet.model.Like;
import socialnet.model.Tag;
import socialnet.model.enums.PostType;
import socialnet.dto.CommentRs;
import socialnet.dto.PersonRs;
import socialnet.dto.PostRs;
import socialnet.repository.CommentsRepository;
import socialnet.repository.LikeRepository;
import socialnet.repository.PersonRepository;
import socialnet.repository.TagRepository;

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
public class PostMapper1 implements RowMapper<PostRs> {

    private final PersonRepository personRepository;
    private final CommentsRepository commentsRepository;
    private final LikeRepository likeRepository;
    private final TagRepository tagRepository;

    @Override
    public PostRs mapRow(ResultSet resultSet, int i) throws SQLException {

        resultSet.next();
        int authorId = resultSet.getInt("author_id");
//        PersonRs author = personRepository.getPersonById(authorId);
        int postId = resultSet.getInt("id");
        List<CommentRs> comments = commentsRepository.getCommentsByEntityId(postId);
        boolean isBlocked = resultSet.getBoolean("is_blocked");
        List<Like> likesList = likeRepository.getLikesByEntityId(postId);
        int likes = likesList.size();
        boolean myLike = containsMyLike(likesList);
        String postText = resultSet.getString("post_text");
        List<Tag> tags = tagRepository.getTagsByPostId(postId);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        Timestamp timestamp = resultSet.getTimestamp("time");
        String time = dateFormat.format(new Date(timestamp.getTime()));
        String title = resultSet.getString("title");
        PostType type = PostType.valueOf(getType(resultSet, timestamp));
        return null;
//        return new PostRs(
//                author,
//                comments,
//                postId,
//                isBlocked,
//                likes,
//                myLike,
//                postText,
//                tags,
//                time,
//                title,
//                type
//                );
    }


    private String getType(ResultSet resultSet, Timestamp timestamp) throws SQLException {
        if (resultSet.getBoolean("is_deleted")) return PostType.DELETED.toString();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (timestamp.after(now)) {
            return PostType.QUEUED.toString();
        } else {
            return PostType.POSTED.toString();
        }
    }

    private boolean containsMyLike(List<Like> likesList) {
        int myId = 0;
        List<Long> personIdList = likesList.stream().map(Like::getPersonId).collect(Collectors.toList());
        return personIdList.contains(myId);
    }
}
