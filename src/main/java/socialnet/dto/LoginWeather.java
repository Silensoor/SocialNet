package socialnet.dto;

import lombok.Data;

@Data
public class LoginWeather {
    private String city;
    private String clouds;
    private String date;
    private String temp;
}
