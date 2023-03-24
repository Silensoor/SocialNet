package socialnet.dto;

import lombok.Data;
import socialnet.dto.login.LoginCurrency;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class PersonRs {
    private String about;
    private Timestamp birthDate;
    private String city;
    private String country;
    private LoginCurrency currency;
    private String email;
    private String firstName;
    private String friendStatus;
    private Long id;
    private Boolean isBlocked;
    private Boolean isBlockedByCurrentUser;
    private String lastName;
    private Date lastOnlineTime;
    private String messagesPermission;
    private String online;
    private String phone;
    private String photo;
    private Timestamp regDate;
    private String token;
    private Timestamp userDeleted;
    private WeatherRs weather;
}
