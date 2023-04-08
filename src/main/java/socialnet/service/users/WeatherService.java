package socialnet.service.users;

import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    public String getWeatherByCity(String city) {

//        WebClient webClient = WebClient.builder().build();
//
//        Mono<String> weather = webClient.get()
//                .uri()
        return "Погода в ".concat(city);
    }
}
