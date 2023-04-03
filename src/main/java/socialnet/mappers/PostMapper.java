package socialnet.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import socialnet.api.response.PostRs;
import socialnet.model.Post;

@Mapper
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    PostRs toDTO(Post post);
}
