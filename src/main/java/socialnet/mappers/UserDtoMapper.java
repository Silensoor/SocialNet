package socialnet.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import socialnet.api.request.UserRq;
import socialnet.api.request.UserUpdateDto;
import socialnet.utils.Converter;

@Mapper(componentModel = "spring")
public abstract class UserDtoMapper {
    @Autowired
    protected Converter converter;
    @Mappings({
            @Mapping(target = "birthDate", expression = "java(converter.dateToTimeStamp(userRq.getBirthDate()))"),
            @Mapping(target = "photo", source = "photoId")
    })
    public abstract UserUpdateDto toDto(UserRq userRq);
}
