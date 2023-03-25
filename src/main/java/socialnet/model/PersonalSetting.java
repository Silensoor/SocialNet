package socialnet.model;

import lombok.Data;

@Data
public class PersonalSetting {
    private Long id;
    private Boolean commentCommentNotification;
    private Boolean friendBirthdayNotification;
    private Boolean likeNotification;
    private Boolean messageNotification;
    private Boolean postCommentNotification;
    private Boolean postNotification;
}
