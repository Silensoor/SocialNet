package socialnet.dto;

import lombok.Data;

import java.util.Date;
@Data
public class DataRs {
    private String about;
    private Date birth_date;
    private String city;
    private String country;
    private CurrencyRs currency;
    private String email;
    private String first_name;
    private Long id;
    private String friend_status;
    private Boolean is_blocked;
    private Boolean is_blocked_by_current_user;
    private String last_name;
    private Date last_online_time;
    private String messages_permission;
    private Boolean online;
    private String phone;
    private String photo;
    private Date reg_date;
    private String token;
    private Boolean user_deleted;
    private WeatherRs weather;
}
