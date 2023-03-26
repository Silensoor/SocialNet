package socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import socialnet.dto.PersonRs;
import socialnet.dto.PostRs;
import socialnet.model.Post;
import socialnet.repository.PersonRepository;

@Mapper(componentModel = "spring", imports = PostMapper.class)
public abstract class PostMapper {
    @Autowired
    protected PersonRepository personRepository;
    @Autowired
    protected PersonMapper personMapper;

    @Mapping(target = "author", expression = "java(getAuthor(post.getId()))")
    public abstract PostRs toDTO(Post post);

    public PersonRs getAuthor(long id) {
        return personMapper.toDTO(personRepository.getPersonById(id));
    }
}
