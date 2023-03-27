package socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import socialnet.dto.CommentRs;
import socialnet.dto.PostRs;
import socialnet.model.Like;
import socialnet.model.Person;
import socialnet.model.Post;
import socialnet.model.Tag;
import socialnet.model.enums.PostType;
import java.sql.Timestamp;
import java.util.List;

@Mapper(componentModel = "spring", imports = PostMapper.class)
public abstract class PostMapper {
    @Mapping(target = "type", expression = "java(getType(post))")
    @Mapping(target = "tags", source = "tags")
    @Mapping(target = "myLike", expression = "java(itLikesMe(likes, authUserId))")
    @Mapping(target = "likes", expression = "java(likes.size())")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "id", source = "post.id")
    @Mapping(target = "blocked", source = "post.blocked")
    @Mapping(target = "author", source = "author")
    public abstract PostRs toDTO(Post post, Person author, List<Like> likes, List<Tag> tags, long authUserId, List<CommentRs> comments);

    PostType getType(Post post) {
        if (post.isDeleted()) return PostType.DELETED;
        Timestamp postTime = post.getTime();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (postTime.after(now)) return PostType.QUEUED;
        return PostType.POSTED;
    }

    boolean itLikesMe(List<Like> likes, long authUserId) {
        for (Like like : likes) {
            if (like.getPersonId() == authUserId) return true;
        }
        return false;
    }
}
