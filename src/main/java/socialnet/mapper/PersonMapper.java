package socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import socialnet.dto.PersonRs;
import socialnet.model.Person;

import java.sql.ResultSet;
import java.sql.SQLException;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonRs toModel(Person person);

    Person toDTO(PersonRs personRs);
}
