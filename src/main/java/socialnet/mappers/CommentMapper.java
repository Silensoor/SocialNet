package socialnet.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import socialnet.api.request.CommentRq;
import socialnet.api.response.CommentRs;
import socialnet.model.Comment;
import socialnet.service.CommentService;
import socialnet.utils.CommentServiceDetails;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    List<CommentRs> toDTO(List<Comment> list);

    @Mapping(target = "subComments", source = "details.subComments")
    @Mapping(target = "myLike", source = "details.myLike")
    @Mapping(target = "likes", source = "details.likes")
    @Mapping(target = "author", source = "details.author")
    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "isBlocked", source = "comment.isBlocked")
    @Mapping(target = "isDeleted", source = "comment.isDeleted")
    @Mapping(target = "postId", source = "comment.postId")
    @Mapping(target = "time", source = "comment.time")
    CommentRs toDTO(Comment comment, CommentServiceDetails details);

    CommentRs toDTO(Comment comment);

    @Mapping(target = "id", source = "details.id")
    @Mapping(target = "time", source = "details.time")
    @Mapping(target = "postId", source = "details.postId")
    @Mapping(target = "isDeleted", source = "details.isDeleted")
    @Mapping(target = "isBlocked", source = "details.isBlocked")
    @Mapping(target = "authorId", source = "details.authorId")
    Comment toModel(CommentRq commentRq, CommentServiceDetails details);
}
