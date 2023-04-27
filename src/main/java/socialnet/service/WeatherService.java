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
import socialnet.mappers.WeatherMapper;
import socialnet.model.Weather;
import socialnet.repository.CityRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherService {
    @Value("${weather.apiKey}")
    private String apiKey;

    private final CityRepository cityRepository;
    private final WeatherMapper weatherMapper;


    public WeatherRs getWeatherByCity(String city) {

        if (city == null) return new WeatherRs();

        if (!cityRepository.containsCity(city)) return new WeatherRs();

        //Чтение информации о погоде из базы данных

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

        if (list.length() == 0) return new WeatherRs();

        return getWeatherRs((JSONObject) list.get(0));
    }

    private WeatherRs getWeatherRs(JSONObject jsonObject) {

        JSONObject weather = (JSONObject) jsonObject.getJSONArray("weather").get(0);

        String openWeatherId = weather.getString("id");
        //запись в базу данных информации о погоде в конкретном городе

        JSONObject main = jsonObject.getJSONObject("main");

        String currentTemp;
        try {
            currentTemp = Integer.toString(Math.round(Float.parseFloat(main.get("temp").toString()) - 273.15F));
        } catch (Exception ex) {
            currentTemp = "?";
        }

        WeatherRs weatherRs = new WeatherRs(
                jsonObject.getString("name"),
                weather.getString("description"),
                LocalDate.now().toString(),
                currentTemp);

        Weather w = weatherMapper.toModel(weatherRs);

        return weatherRs;
    }

    private void saveWeather() {

    }
}
