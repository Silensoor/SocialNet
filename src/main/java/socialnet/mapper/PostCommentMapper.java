package socialnet.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import socialnet.model.Like;
import socialnet.dto.CommentRs;
import socialnet.dto.PersonRs;
import socialnet.model.Person;
import socialnet.model.PostComment;
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

@Mapper(componentModel = "spring")
public abstract class PostCommentMapper {

    @Mapping(target = "isDeleted", source = "postComment.isDeleted")
    @Mapping(target = "isBlocked", source = "postComment.isDeleted")
    @Mapping(target = "subComments", source = "subComments")
    @Mapping(target = "myLike", expression = "java(itLikesMe(likes, authUserId))")
    @Mapping(target = "likes", expression = "java(likes.size())")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "id", source = "postComment.id")
    public abstract CommentRs toDTO(Person author, PostComment postComment, List<CommentRs> subComments, List<Like> likes, long authUserId);

    boolean itLikesMe(List<Like> likes, long authUserId) {
        for (Like like : likes) {
            if (like.getPersonId() == authUserId) return true;
        }
        return false;
    }

}
