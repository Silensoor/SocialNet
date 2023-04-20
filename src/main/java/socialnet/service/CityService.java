package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.dto.geolocation.GeolocationRs;
import socialnet.repository.CityRepository;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepository;

    public List<GeolocationRs> getCitiesByCountryAndStarts(String country, String starts) {
        return cityRepository.getCitiesByStarts(country, starts).stream()
                .map(c -> new GeolocationRs(c.getName())).collect(Collectors.toList());
    }
    public List<GeolocationRs> getCitiesByCountry(String country) {
        return cityRepository.getCitiesByCountry(country).stream()
                .map(c -> new GeolocationRs(c.getName())).collect(Collectors.toList());
    }
}
