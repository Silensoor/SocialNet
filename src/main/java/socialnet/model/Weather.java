package socialnet.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Weather {
    private int id;
    private int gismeteoId;
    private double temperature;
    private String description;
    private Timestamp time;
}
