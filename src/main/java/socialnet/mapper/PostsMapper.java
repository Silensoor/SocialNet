package socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import socialnet.api.response.PostRs;
import socialnet.dto.PostRq;
import socialnet.model.Like;
import socialnet.model.Post;
import socialnet.model.enums.PostType;
import socialnet.service.PostService;
import java.sql.Timestamp;
import java.util.List;

@Mapper(componentModel = "spring", imports = PostsMapper.class)
public abstract class PostsMapper {
    @Mapping(target = "type", expression = "java(getType(post))")
    @Mapping(target = "tags", source = "details.tags")
    @Mapping(target = "myLike", expression = "java(itLikesMe(details.getLikes(), details.getAuthUserId()))")
    @Mapping(target = "likes", expression = "java(details.getLikes().size())")
    @Mapping(target = "comments", source = "details.comments")
    @Mapping(target = "id", source = "post.id")
    @Mapping(target = "isBlocked", source = "post.isBlocked")
    @Mapping(target = "author", source = "details.author")
    public abstract PostRs toRs(Post post, PostService.Details details);

    @Mapping(target = "timeDelete", ignore = true)
    @Mapping(target = "time", expression = "java(getTime(publishDate))")
    @Mapping(target = "postText", source = "postRq.post_text")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", expression = "java(getFalse())")
    @Mapping(target = "isBlocked", expression = "java(getFalse())")
    @Mapping(target = "authorId", source = "id")
    public abstract Post toModel(PostRq postRq, Integer publishDate, int id);


    String getType(Post post) {
        if (post.getIsDeleted()) return PostType.DELETED.toString();
        Timestamp postTime = post.getTime();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (postTime.after(now)) return PostType.QUEUED.toString();
        return PostType.POSTED.toString();
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
            if (like.getPersonId().equals(authUserId)) return true;
        }
        return false;
    }
}
