package socialnet.mapper;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import socialnet.dto.PersonRs;
import socialnet.dto.PostRs;
import socialnet.model.Person;
import socialnet.model.Post;
import socialnet.repository.PersonRepository;

@Mapper(componentModel = "spring", imports = Builder.class)
@NoArgsConstructor
public abstract class PostMapper {
    @Autowired
    PersonRepository personRepository;



    @Mapping(target = "author", expression = "java(new Builder().getAuthor(post.getId()))")
    public abstract PostRs toDTO(Post post);

    private PersonRs getPersonRs() {

        return new PersonRs();
    }

}
