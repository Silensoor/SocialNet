package socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import socialnet.dto.CommentRs;
import socialnet.dto.PostRq;
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
    public abstract PostRs toRs(Post post, Person author, List<Like> likes, List<Tag> tags, long authUserId, List<CommentRs> comments);

    @Mapping(target = "timeDelete", ignore = true)
    @Mapping(target = "time", expression = "java(getTime(publishDate))")
    @Mapping(target = "postText", source = "postRq.post_text")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", expression = "java(getFalse())")
    @Mapping(target = "blocked", expression = "java(getFalse())")
    @Mapping(target = "authorId", source = "id")
    public abstract Post toModel(PostRq postRq, Integer publishDate, int id);


    PostType getType(Post post) {
        if (post.isDeleted()) return PostType.DELETED;
        Timestamp postTime = post.getTime();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (postTime.after(now)) return PostType.QUEUED;
        return PostType.POSTED;
    }

    Timestamp getTime(Integer publishDate) {
        if (publishDate == null) return new Timestamp(System.currentTimeMillis());
        return new Timestamp(publishDate);
    }

    boolean getFalse() {
        return false;
    }

    boolean itLikesMe(List<Like> likes, long authUserId) {
        for (Like like : likes) {
            if (like.getPersonId() == authUserId) return true;
        }
        return false;
    }
}
