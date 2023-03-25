package socialnet.model.db;

import lombok.Data;

@Data
public class PersonalSetting {
    private long id;
    private boolean commentCommentNotification;
    private boolean friendBirthdayNotification;
    private boolean likeNotification;
    private boolean messageNotification;
    private boolean postCommentNotification;
    private boolean postNotification;
}
