package socialnet.model;

import lombok.Data;

@Data
public class PersonSettings {
    private Long id;

    private Boolean commentCommentNotification;

    private Boolean friendBirthdayNotification;

    private Boolean friendRequest;

    private Boolean likeNotification;

    private Boolean messageNotification;

    private Boolean postCommentNotification;

    private Boolean postNotification;
}
