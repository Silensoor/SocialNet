package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import socialnet.api.response.CommonRs;
import socialnet.service.CityService;
import socialnet.service.GeolocationsService;

@RestController
@RequestMapping("/api/v1/geolocations")
@RequiredArgsConstructor
public class GeolocationsController {
    private final GeolocationsService geolocationsService;
    private final CityService cityService;
    @GetMapping("cities/api")
    public CommonRs getCitiesFromApiStartsWith(@RequestParam("country") String country,
                                               @RequestParam("starts") String starts) {
        return new CommonRs(cityService.getCitiesByCountryAndStarts(country, starts));
    }

    @GetMapping("cities/db")
    public ResponseEntity<?> getCitiesByStarts(@RequestParam("country") String country,
                                               @RequestParam("starts") String starts) {
        return ResponseEntity.ok(new CommonRs(cityService.getCitiesByCountryAndStarts(country, starts)));
    }

    @GetMapping("cities/uses")
    public ResponseEntity<?> getCitiesByCountry(@RequestParam("country") String country) {
        return ResponseEntity.ok(new CommonRs(cityService.getCitiesByCountry(country)));
    }

    @GetMapping("countries")
    public ResponseEntity<?> getCountries() {
        return ResponseEntity.ok(new CommonRs(geolocationsService.findAllCountry()));
    }

}
