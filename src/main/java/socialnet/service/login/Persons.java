package socialnet.service.login;

import lombok.Data;

import java.util.Date;

@Data
public class Persons {

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getChange_password_token() {
        return change_password_token;
    }

    public void setChange_password_token(String change_password_token) {
        this.change_password_token = change_password_token;
    }

    public Integer getConfiguration_code() {
        return configuration_code;
    }

    public void setConfiguration_code(Integer configuration_code) {
        this.configuration_code = configuration_code;
    }

    public Date getDeleted_time() {
        return deleted_time;
    }

    public void setDeleted_time(Date deleted_time) {
        this.deleted_time = deleted_time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public Boolean getIs_approved() {
        return is_approved;
    }

    public void setIs_approved(Boolean is_approved) {
        this.is_approved = is_approved;
    }

    public Boolean getIs_blocked() {
        return is_blocked;
    }

    public void setIs_blocked(Boolean is_blocked) {
        this.is_blocked = is_blocked;
    }

    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public Date getLast_online_time() {
        return last_online_time;
    }

    public void setLast_online_time(Date last_online_time) {
        this.last_online_time = last_online_time;
    }

    public String getMessage_permissions() {
        return message_permissions;
    }

    public void setMessage_permissions(String message_permissions) {
        this.message_permissions = message_permissions;
    }

    public String getNotifications_session_id() {
        return notifications_session_id;
    }

    public void setNotifications_session_id(String notifications_session_id) {
        this.notifications_session_id = notifications_session_id;
    }

    public String getOnline_status() {
        return online_status;
    }

    public void setOnline_status(String online_status) {
        this.online_status = online_status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getReg_date() {
        return reg_date;
    }

    public void setReg_date(String reg_date) {
        this.reg_date = reg_date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getTelegram_id() {
        return telegram_id;
    }

    public void setTelegram_id(Integer telegram_id) {
        this.telegram_id = telegram_id;
    }

    public Integer getPerson_settings_id() {
        return person_settings_id;
    }

    public void setPerson_settings_id(Integer person_settings_id) {
        this.person_settings_id = person_settings_id;
    }
}
