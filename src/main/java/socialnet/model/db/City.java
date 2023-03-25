package socialnet.model.db;

import lombok.Data;

@Data
public class City {
    private long id;
    private String name;
    private int gismeteo_id;
    private long country_id;
    private String district;
    private String subDistrict;
}
