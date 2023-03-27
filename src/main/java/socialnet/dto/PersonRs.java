package socialnet.dto;

<<<<<<< HEAD
import lombok.Data;
import socialnet.dto.login.LoginCurrency;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class PersonRs {
    private String about;
    private String birthDate;
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
    private String regDate;
    private String token;
    private Boolean userDeleted;
=======
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class PersonRs {

    private String about;

    @JsonProperty("birth_date")
    private String birthDate;

    private String city;

    private String country;

    private CurrencyRs currency;

    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("friend_status")
    private String friendStatus;

    private Long id;

    @JsonProperty("is_blocked")
    private Boolean isBlocked;

    @JsonProperty("is_blocked_by_current_user")
    private Boolean isBlockedByCurrentUser;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("last_online_time")
    private String lastOnlineTime;

    @JsonProperty("messages_permission")
    private String messagesPermission;

    private Boolean online;

    private String phone;

    private String photo;

    @JsonProperty("reg_date")
    private String regDate;

    private String token;

    @JsonProperty("user_deleted")
    private Boolean userDeleted;

>>>>>>> origin/dev
    private WeatherRs weather;
}
