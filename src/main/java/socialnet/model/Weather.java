package socialnet.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Weather {
    private Integer id;
    private Integer gismeteoId;
    private Double temperature;
    private String description;
    private Timestamp time;
}
