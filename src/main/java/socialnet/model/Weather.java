package socialnet.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Weather {
    private Long openWeatherId;
    private String city;
    private String clouds;
    private Float temp;
    private Timestamp date;
}
