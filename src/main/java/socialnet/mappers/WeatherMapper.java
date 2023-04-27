package socialnet.mappers;

import org.mapstruct.Mapper;
import socialnet.api.response.WeatherRs;
import socialnet.model.Weather;

@Mapper(componentModel = "spring")
public interface WeatherMapper {
    Weather toModel(WeatherRs weatherRs);
}
