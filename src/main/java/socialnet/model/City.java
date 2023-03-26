package socialnet.model;

import lombok.Data;

@Data
public class City {
    private Long id;
    private String name;
    private Integer gismeteo_id;
    private Long country_id;
    private String district;
    private String subDistrict;
}
