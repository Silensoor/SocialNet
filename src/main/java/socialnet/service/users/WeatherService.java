package socialnet.service.users;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
