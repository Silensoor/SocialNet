package socialnet.dto;

import lombok.Data;

import java.util.Date;

@Data
public class WeatherRs {

    private String city;

    private String clouds;

    private Date date;

    private String temp;
}
