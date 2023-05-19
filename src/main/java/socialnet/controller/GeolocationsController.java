package socialnet.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import socialnet.api.response.CommonRs;
import socialnet.service.GeolocationsService;
import socialnet.dto.geolocation.GeolocationRs;

import java.util.List;

@RestController
@RequestMapping("/api/v1/geolocations")
@RequiredArgsConstructor
@Tag(name = "geolocations-controller", description = "Create or get records about cities and countries")
public class GeolocationsController {
    private final GeolocationsService geolocationsService;

    @GetMapping("cities/api")
    public CommonRs<List<GeolocationRs>> getCitiesFromApiStartsWith(@RequestParam("country") String country,
                                                                    @RequestParam("starts") String starts) {
        return new CommonRs<>(geolocationsService.getCitiesByCountryAndStarts(country, starts));
    }

    @GetMapping("cities/db")
    public CommonRs<List<GeolocationRs>> getCitiesByStarts(@RequestParam("country") String country,
                                                           @RequestParam("starts") String starts) {
        return new CommonRs<>(geolocationsService.getCitiesByCountryAndStarts(country, starts));
    }

    @GetMapping("cities/uses")
    public CommonRs<List<GeolocationRs>> getCitiesByCountry(@RequestParam("country") String country) {
        return new CommonRs<>(geolocationsService.getCitiesByCountry(country));
    }

    @GetMapping("countries")
    public CommonRs<List<GeolocationRs>> getCountries() {
        return new CommonRs<>(geolocationsService.findAllCountry());
    }

}
