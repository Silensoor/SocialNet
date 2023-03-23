package socialnet.security;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Long id;
    private String about;
    private String birth_date;
    private String change_password_token;
    private Integer configuration_code;
    private Date deleted_time;
    private String email;
    private String first_name;
    private Boolean is_approved;
    private Boolean is_blocked;
    private Boolean is_deleted;
    private String last_name;
    private Date last_online_time;
    private String message_permissions;
    private String notifications_session_id;
    private String online_status;
    private String password;
    private String phone;
    private String photo;
    private String reg_date;
    private String city;
    private String country;
    private Integer telegram_id;
    private Integer person_settings_id;




}
