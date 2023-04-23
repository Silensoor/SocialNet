package socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import socialnet.api.response.CommentRs;
import socialnet.model.Comment;
import socialnet.model.Like;
import socialnet.model.Person;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PostCommentMapper {

    @Mapping(target = "isDeleted", source = "postComment.isDeleted")
    @Mapping(target = "isBlocked", source = "postComment.isDeleted")
    @Mapping(target = "subComments", source = "subComments")
    @Mapping(target = "myLike", expression = "java(itLikesMe(likes, authUserId))")
    @Mapping(target = "likes", expression = "java(likes.size())")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "id", source = "postComment.id")
    public abstract CommentRs toDTO(Person author, Comment postComment, List<CommentRs> subComments, List<Like> likes, long authUserId);

    boolean itLikesMe(List<Like> likes, long authUserId) {
        for (Like like : likes) {
            if (like.getPersonId() == authUserId) return true;
        }
        return false;
    }
}
