package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import socialnet.api.response.WeatherRs;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WeatherService {
    @Value("${weather.apiKey}")
    private String apiKey;


    public WeatherRs getWeatherByCity(String city) {

        if (city == null) return new WeatherRs();

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("api.openweathermap.org/data/2.5/find")
                .path("/")
                .query("q={City}&type={Type}&APPID={ApiKey}")
                .buildAndExpand(city, "like", apiKey);


        WebClient webClient = WebClient.builder().build();
        String weatherJson = webClient.get()
                .uri(uriComponents.toUriString())
                .retrieve().bodyToMono(String.class).block();

        JSONObject jsonObject = new JSONObject(weatherJson);

        JSONArray list = jsonObject.getJSONArray("list");

        JSONObject s = (JSONObject) list.get(0);

        return getWeatherRs(s);
    }

    private WeatherRs getWeatherRs(JSONObject jsonObject) {

        JSONObject weather = (JSONObject) jsonObject.getJSONArray("weather").get(0);
        //String cityId = weather.getString("id");

        JSONObject main = jsonObject.getJSONObject("main");

        String currentTemp;
        try {
            currentTemp = Integer.toString(Math.round(main.getFloat("temp") - 273.15F));
        } catch (Exception e) {
            currentTemp = "?";
        }

        return new WeatherRs(jsonObject.getString("name"),
                weather.getString("description"),
                LocalDate.now().toString(),
                currentTemp);
    }
}
