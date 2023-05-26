package socialnet.model;

import lombok.Data;

@Data
public class SearchOptions {

    private String jwtToken;
    private String author;
    private String firstName;
    private String lastName;
    private Long dateFrom;
    private Long dateTo;
    private String[] tags;
    private String text;
    private Integer ageFrom;
    private Integer ageTo;
    private String city;
    private String country;
    private Boolean flagQueryAll;
    private Integer id;
    private Integer offset;
    private Integer perPage;
    private Integer authorId;
}
