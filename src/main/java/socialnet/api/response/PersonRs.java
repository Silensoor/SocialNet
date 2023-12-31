package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonRs {

    private String about;

    @JsonProperty("birth_date")
    private Timestamp birthDate;

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
    private Timestamp lastOnlineTime;

    @JsonProperty("messages_permission")
    private String messagesPermission;

    private Boolean online;

    private String phone;

    private String photo;

    @JsonProperty("reg_date")
    private Timestamp regDate;

    private String token;

    @JsonProperty("user_deleted")
    private Boolean userDeleted;

    private WeatherRs weather;

}
