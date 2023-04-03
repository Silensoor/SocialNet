package socialnet.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import socialnet.api.response.PostRs;
import socialnet.mapper.PostsMapper;
import socialnet.model.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    PostRs toDTO(Post post);
}
