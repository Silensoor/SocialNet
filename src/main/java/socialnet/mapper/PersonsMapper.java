package socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import socialnet.api.response.CurrencyRs;
import socialnet.api.response.PersonRs;
import socialnet.model.Person;


@Mapper(componentModel = "spring", imports = PersonsMapper.class)
public interface PersonsMapper {

    static boolean isBlockedByCurrentUser() {
        return true;
    }

    static CurrencyRs getCurrencyRs() {
        return new CurrencyRs("euro", "usd");
    }

    @Mapping(target = "currency", expression = "java(PersonsMapper.getCurrencyRs())")
    @Mapping(target = "isBlockedByCurrentUser", expression = "java(PersonsMapper.isBlockedByCurrentUser())")
    PersonRs toDTO(Person person);
}