package socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import socialnet.dto.CurrencyRs;
import socialnet.dto.PersonRs;
import socialnet.model.Person;


@Mapper(componentModel = "spring", imports = PersonMapper.class)
public interface PersonMapper {

    static boolean isBlockedByCurrentUser() {
        return true;
    }

    static CurrencyRs getCurrencyRs() {
        return new CurrencyRs("euro", "usd");
    }

    @Mapping(target = "currency", expression = "java(PersonMapper.getCurrencyRs())")
    @Mapping(target = "isBlockedByCurrentUser", expression = "java(PersonMapper.isBlockedByCurrentUser())")
    PersonRs toDTO(Person person);
}