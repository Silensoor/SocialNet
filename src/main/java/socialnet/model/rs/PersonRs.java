package socialnet.model.rs;

import lombok.Data;
import socialnet.model.enums.FriendStatus;

@Data
public class PersonRs {

    private String about;
    private String birthDate;
    private String city;
    private String country;
    private CurrencyRs currency;
    private String email;
    private String firstName;
    private FriendStatus friendStatus;
    private boolean isBlocked;
    private boolean isBlockedByCurrentUser;
    private String lastName;
    private String lastOnlineTime;
    private String messagePermission;
    private boolean online;
    private String phone;
    private String photo;
    private String regDate;
    private String token;
    private boolean userDeleted;
    private WeatherRs weather;
}
