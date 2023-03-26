package socialnet.mapper;

import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import socialnet.dto.CurrencyRs;
import socialnet.dto.PersonRs;
import socialnet.model.Person;

import java.sql.ResultSet;
import java.sql.SQLException;

@Mapper(componentModel = "spring", imports = PersonMapper.class)
public interface PersonMapper {

    static boolean isBlockedByCurrentUser() {
        return true;
    }
    static CurrencyRs getCurrencyRs() {
        return new CurrencyRs("euro", "usd");
    }

    @Mapping(target = "currency", expression = "java(PersonMapper.getCurrencyRs())")
    @Mapping(target = "blockedByCurrentUser", expression = "java(PersonMapper.isBlockedByCurrentUser())")
    PersonRs toDTO(Person person);
}
