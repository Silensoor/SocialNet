package socialnet.dto;

import lombok.Data;

@Data
public class LoginRs {
    private PersonRs data;
    private String email;
    private String firstName;
    private String friendStatus;
    private Integer id;
    private Boolean isBlocked;
    private Boolean isBlockedByCurrentUser;
    private String lastName;
    private String lastOnlineTime;
    private String messagesPermission;
    private Boolean online;
    private String phone;
    private String photo;
    private String regDate;
    private String token;
    private Boolean userDeleted;
    private WeatherRs weather;

}
